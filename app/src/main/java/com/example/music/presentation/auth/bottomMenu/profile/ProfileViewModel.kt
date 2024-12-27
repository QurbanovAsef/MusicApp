package com.example.music.presentation.auth.bottomMenu.profile

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    val language: MutableLiveData<String> = MutableLiveData("English")
    val theme: MutableLiveData<String> = MutableLiveData("light")

    private val sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//
//    init {
//        // Əgər əvvəlcədən dil və tema saxlanılıbsa, onu yüklə
//        language.value = sharedPreferences.getString("language", "English")
//        theme.value = sharedPreferences.getString("theme", "light")
//    }
    fun setLanguage(language: String, context: Context) {
        if (this.language.value != language) {
            this.language.value = language
            saveLanguagePreference(language, context)
            changeAppLanguage(language, context)
        }
    }

    fun setTheme(theme: String, context: Context) {
        if (this.theme.value != theme) {
            this.theme.value = theme
            saveThemePreference(theme, context)
            changeAppTheme(theme, context)
        }
    }


    private fun changeAppLanguage(language: String, context: Context) {
        val locale = when (language) {
            "English" -> java.util.Locale("en")
            "Azərbaycan" -> java.util.Locale("az")
            else -> java.util.Locale("en")
        }
        val config = context.resources.configuration
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }

    private fun changeAppTheme(theme: String, context: Context) {
        AppCompatDelegate.setDefaultNightMode(
            if (theme == "dark") AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun saveLanguagePreference(language: String, context: Context) {
        if (sharedPreferences.getString("language", "") != language) {
            sharedPreferences.edit().putString("language", language).apply()
        }
    }

    private fun saveThemePreference(theme: String, context: Context) {
        if (sharedPreferences.getString("theme", "") != theme) {
            sharedPreferences.edit().putString("theme", theme).apply()
        }
    }

}
