package com.example.music

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplicationMusic : Application() {

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE)
        val theme = sharedPreferences.getString("theme", "light")
        val language = sharedPreferences.getString("language", "English")

        // Tema və dil seçimini tətbiq et
        applyTheme(theme)
        applyLanguage(language)
    }

    private fun applyTheme(theme: String?) {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun applyLanguage(language: String?) {
        val locale = when (language) {
            "English" -> java.util.Locale("en")
            "Azərbaycan" -> java.util.Locale("az")
            else -> java.util.Locale("en")
        }
        val config = resources.configuration
        config.setLocale(locale)
        createConfigurationContext(config)
    }
}
