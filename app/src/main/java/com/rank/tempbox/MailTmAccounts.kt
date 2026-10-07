package com.rank.tempbox

import android.util.Log
import kotlinx.coroutines.withTimeout

object MailTmAccounts {
    private const val TAG = "TempBox"

    suspend fun deleteCurrent(slot: Int = 1) {
        val token = Integrity.token(slot) ?: return
        val bearer = "Bearer $token"
        try {
            var id = Integrity.accountId(slot)
            if (id.isNullOrBlank()) {
                id = withTimeout(10_000) {
                    RetrofitClient.authenticatedApi.getMe(bearer).id
                }
            }
            if (id.isNullOrBlank()) return
            withTimeout(10_000) {
                RetrofitClient.authenticatedApi.deleteAccount(bearer, id)
            }
            Log.d(TAG, "Deleted mail.tm account $id")
        } catch (e: Exception) {
            Log.w(TAG, "Remote account delete failed", e)
        }
    }
}
