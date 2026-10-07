package com.rank.tempbox

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object Integrity {
    private const val FILE = "tempbox_secure"
    private const val FALLBACK_FILE = "tempbox_secure_fallback"
    private const val TAG = "TempBox-Integrity"

    @Volatile
    private var prefs: SharedPreferences? = null
    private val lock = Any()

    fun init(context: Context) {
        if (prefs != null) return
        synchronized(lock) {
            if (prefs != null) return
            val app = context.applicationContext
            prefs = createSecurePrefs(app)
            migrateSecretsFromPlain(app)
        }
    }

    private fun createSecurePrefs(context: Context): SharedPreferences {
        return try {
            encrypted(context)
        } catch (e: Exception) {
            Log.w(TAG, "Secure prefs unreadable, recreating", e)
            context.deleteSharedPreferences(FILE)
            try {
                encrypted(context)
            } catch (e2: Exception) {
                Log.e(TAG, "Encrypted prefs unavailable, using private fallback", e2)
                context.getSharedPreferences(FALLBACK_FILE, Context.MODE_PRIVATE)
            }
        }
    }

    private fun encrypted(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            context,
            FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun p(): SharedPreferences =
        prefs ?: throw IllegalStateException("Integrity.init() must be called before use")

    fun putInt(key: String, value: Int) { p().edit().putInt(key, value).apply() }
    fun getInt(key: String, default: Int): Int = p().getInt(key, default)

    fun putLong(key: String, value: Long) { p().edit().putLong(key, value).apply() }
    fun getLong(key: String, default: Long): Long = p().getLong(key, default)

    fun putBoolean(key: String, value: Boolean) { p().edit().putBoolean(key, value).apply() }
    fun getBoolean(key: String, default: Boolean): Boolean = p().getBoolean(key, default)

    fun putString(key: String, value: String) { p().edit().putString(key, value).apply() }
    fun getString(key: String, default: String?): String? = p().getString(key, default)

    fun remove(key: String) { p().edit().remove(key).apply() }

    fun clear() { p().edit().clear().apply() }

    fun email(slot: Int): String? =
        getString(PrefKeys.email(slot), null)?.takeIf { it.isNotBlank() }

    fun password(slot: Int): String? =
        getString(PrefKeys.password(slot), null)?.takeIf { it.isNotBlank() }

    fun token(slot: Int): String? =
        getString(PrefKeys.token(slot), null)?.takeIf { it.isNotBlank() }

    fun accountId(slot: Int): String? =
        getString(PrefKeys.accountId(slot), null)?.takeIf { it.isNotBlank() }

    fun saveAccount(slot: Int, email: String, password: String, token: String, accountId: String = "") {
        p().edit()
            .putString(PrefKeys.email(slot), email)
            .putString(PrefKeys.password(slot), password)
            .putString(PrefKeys.token(slot), token)
            .apply()
        if (accountId.isNotBlank()) {
            putString(PrefKeys.accountId(slot), accountId)
        }
    }

    fun saveToken(slot: Int, token: String, accountId: String = "") {
        putString(PrefKeys.token(slot), token)
        if (accountId.isNotBlank()) {
            putString(PrefKeys.accountId(slot), accountId)
        }
    }

    fun clearAccount(slot: Int) {
        p().edit()
            .remove(PrefKeys.email(slot))
            .remove(PrefKeys.password(slot))
            .remove(PrefKeys.token(slot))
            .remove(PrefKeys.createdAt(slot))
            .remove(PrefKeys.accountId(slot))
            .apply()
    }

    private fun migrateSecretsFromPlain(context: Context) {
        val plain = context.getSharedPreferences(PrefKeys.PREFS_NAME, Context.MODE_PRIVATE)
        val slot = 1
        if (email(slot) == null) {
            val migratedEmail = plain.getString(PrefKeys.email(slot), null)
                ?: plain.getString("email_address", null)
            val migratedPassword = plain.getString(PrefKeys.password(slot), null)
                ?: plain.getString("email_password", null)
            val migratedToken = plain.getString(PrefKeys.token(slot), null)
                ?: plain.getString("auth_token", null)
            if (!migratedEmail.isNullOrBlank() && !migratedPassword.isNullOrBlank()) {
                p().edit()
                    .putString(PrefKeys.email(slot), migratedEmail)
                    .putString(PrefKeys.password(slot), migratedPassword)
                    .putString(PrefKeys.token(slot), migratedToken.orEmpty())
                    .apply()
            }
        }
        plain.edit()
            .remove(PrefKeys.email(slot))
            .remove(PrefKeys.password(slot))
            .remove(PrefKeys.token(slot))
            .remove("email_address")
            .remove("email_password")
            .remove("auth_token")
            .apply()
    }
}
