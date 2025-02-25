package com.example.music

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.music.utils.AppConst.LANG_KEY_DEFAULT
import com.example.music.utils.AppConst.LANG_KEY_LANGUAGE
import com.example.music.utils.AppConst.SHARED_KEY_PREFERENCES
import com.example.music.utils.AppConst.SHARED_KEY_THEME
import com.example.music.utils.AppConst.THEME_KEY_DARK
import com.example.music.utils.AppConst.THEME_KEY_DEFAULT
import com.example.music.utils.LocaleUtil
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

@HiltAndroidApp
class MyApplicationMusic : Application() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        // Firebase başlat
        FirebaseApp.initializeApp(this)

        // Firebase App Check aktiv et (Debug üçün)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )

        sharedPreferences = getSharedPreferences(SHARED_KEY_PREFERENCES, MODE_PRIVATE)
        val theme = sharedPreferences.getString(SHARED_KEY_THEME, THEME_KEY_DEFAULT) ?: THEME_KEY_DEFAULT

        val nightMode = getNightMode(theme)
        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            applyTheme(nightMode)
        }
    }

    private fun applyTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun getNightMode(theme: String): Int {
        return when (theme) {
            THEME_KEY_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            else -> AppCompatDelegate.MODE_NIGHT_NO
        }
    }

    override fun attachBaseContext(base: Context) {
        sharedPreferences = base.getSharedPreferences(SHARED_KEY_PREFERENCES, MODE_PRIVATE)

        val language = sharedPreferences.getString(LANG_KEY_LANGUAGE, LANG_KEY_DEFAULT) ?: LANG_KEY_DEFAULT

        super.attachBaseContext(LocaleUtil.getLocalizedContext(base, language))
    }
}
