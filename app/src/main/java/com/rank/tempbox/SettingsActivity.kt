package com.rank.tempbox

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.view.WindowCompat
import com.rank.tempbox.ui.screens.PrivacyPolicyScreen
import com.rank.tempbox.ui.screens.SettingsScreen
import com.rank.tempbox.ui.theme.TempBoxTheme

class SettingsActivity : ComponentActivity() {

    private val viewModel: MainViewModel by lazy {
        (application as TempBoxApplication).mainViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            val prefs = remember { getSharedPreferences(PrefKeys.PREFS_NAME, MODE_PRIVATE) }
            var themeMode by remember {
                mutableStateOf(themeModeFrom(prefs))
            }
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }

            TempBoxTheme(isDark = isDark) {
                var showPrivacy by remember { mutableStateOf(false) }
                if (showPrivacy) {
                    PrivacyPolicyScreen(onBack = { showPrivacy = false })
                } else {
                SettingsScreen(
                    viewModel = viewModel,
                    isDark = isDark,
                    themeMode = themeMode,
                    onThemeModeChanged = { newMode ->
                        themeMode = newMode
                        prefs.edit().putString("theme_mode", newMode).apply()
                        applyNightMode(newMode)
                    },
                    onBack = { finish() },
                    onFullReset = {
                        viewModel.resetApp {
                            getSharedPreferences(PrefKeys.PREFS_NAME, MODE_PRIVATE).edit().clear().apply()
                            Integrity.clear()
                            (application as TempBoxApplication).discardMainViewModel()
                            startActivity(Intent(this, OnboardingActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                            finish()
                        }
                    },
                    onResetOnboarding = {
                        prefs.edit().putBoolean("onboarding_complete", false).apply()
                        startActivity(Intent(this, OnboardingActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        })
                        finish()
                    },
                    onOpenPrivacy = { showPrivacy = true },
                )
                }
            }
        }
    }
}
