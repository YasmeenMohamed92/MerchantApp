
package com.example.merchantapp.core.localization

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LanguageManager {

    fun changeLanguage(languageCode: String) {
        val locales = LocaleListCompat.forLanguageTags(languageCode)

        AppCompatDelegate.setApplicationLocales(locales)
    }

    fun getCurrentLanguage(): String {
        val locales = AppCompatDelegate.getApplicationLocales()

        return if (locales.isEmpty) {
            "en"
        } else {
            locales[0]?.language ?: "en"
        }
    }
}
