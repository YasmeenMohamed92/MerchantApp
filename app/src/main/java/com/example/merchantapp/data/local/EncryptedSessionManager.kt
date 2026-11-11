package com.example.merchantapp.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedSessionManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences = EncryptedSharedPreferences.create(
        context,
        PREF_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(
        merchantId: String,
        pin: String
    ) {
        preferences.edit()
            .putString(KEY_MERCHANT_ID, merchantId)
            .putString(KEY_PIN, pin)
            .putBoolean(KEY_REMEMBER_ME, true)
            .apply()
    }

    fun getSavedMerchantId(): String? {
        if (!isRememberMeEnabled()) return null

        return preferences.getString(
            KEY_MERCHANT_ID,
            null
        )
    }

    fun getSavedPin(): String? {
        if (!isRememberMeEnabled()) return null

        return preferences.getString(
            KEY_PIN,
            null
        )
    }

    fun isRememberMeEnabled(): Boolean {
        return preferences.getBoolean(
            KEY_REMEMBER_ME,
            false
        )
    }

    fun clearCredentials() {
        preferences.edit()
            .remove(KEY_MERCHANT_ID)
            .remove(KEY_PIN)
            .putBoolean(KEY_REMEMBER_ME, false)
            .apply()
    }

    companion object {
        private const val PREF_FILE_NAME = "merchant_encrypted_session"

        private const val KEY_MERCHANT_ID = "merchant_id"
        private const val KEY_PIN = "merchant_pin"
        private const val KEY_REMEMBER_ME = "remember_me"
    }
}
