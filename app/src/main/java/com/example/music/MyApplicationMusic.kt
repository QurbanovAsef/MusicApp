package com.example.music

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale

@HiltAndroidApp
class MyApplicationMusic : Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val theme = sharedPreferences.getString("theme", "light") ?: "light"
        val language = sharedPreferences.getString("language", "English") ?: "English"

        if (AppCompatDelegate.getDefaultNightMode() != getNightMode(theme)) {
            applyTheme(theme)
        }

        val currentLocale = resources.configuration.locales[0]
        val newLocale = when (language) {
            "English" -> Locale("en")
            "Azərbaycan" -> Locale("az")
            else -> Locale("en")
        }

        if (currentLocale != newLocale) {
            applyLanguage(language)
        }
    }

    private fun applyTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(getNightMode(theme))
    }

    private fun applyLanguage(language: String) {
        val locale = when (language) {
            "English" -> Locale("en")
            "Azərbaycan" -> Locale("az")
            else -> Locale("en")
        }
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
    }

    private fun getNightMode(theme: String): Int {
        return when (theme) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
    }
}