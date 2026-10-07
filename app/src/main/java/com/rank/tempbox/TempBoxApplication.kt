package com.rank.tempbox

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun themeModeFrom(prefs: SharedPreferences): String {
    return if (prefs.contains("theme_mode")) {
        prefs.getString("theme_mode", "dark") ?: "dark"
    } else if (prefs.contains("dark_mode")) {
        if (prefs.getBoolean("dark_mode", true)) "dark" else "light"
    } else {
        "dark"
    }
}

fun applyNightMode(mode: String) {
    val night = when (mode) {
        "light" -> AppCompatDelegate.MODE_NIGHT_NO
        "dark" -> AppCompatDelegate.MODE_NIGHT_YES
        else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(night)
}

class TempBoxApplication : Application(), ViewModelStoreOwner {

    private val appViewModelStore = ViewModelStore()
    private val viewModelFactory by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }
    private val vmLock = Any()
    @Volatile private var cachedViewModel: MainViewModel? = null

    val mainViewModel: MainViewModel
        get() {
            synchronized(vmLock) {
                cachedViewModel?.let { return it }
                val created = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
                cachedViewModel = created
                return created
            }
        }

    fun discardMainViewModel() {
        synchronized(vmLock) {
            appViewModelStore.clear()
            cachedViewModel = null
        }
    }

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences(PrefKeys.PREFS_NAME, Context.MODE_PRIVATE)
        applyNightMode(themeModeFrom(prefs))
        com.rank.tempbox.ads.UnityAdsManager.initialize(this)
        createNotificationChannels(this)
        Integrity.init(this)
        scheduleMailboxCleanup()
        mainViewModel

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            android.util.Log.e("TempBox", "Uncaught exception on thread: ${thread.name}", throwable)
            originalHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun scheduleMailboxCleanup() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val cleanupRequest = PeriodicWorkRequestBuilder<MailboxCleanupWorker>(
            4, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "mailbox_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupRequest
        )
    }

    private val originalHandler = Thread.getDefaultUncaughtExceptionHandler()
}
