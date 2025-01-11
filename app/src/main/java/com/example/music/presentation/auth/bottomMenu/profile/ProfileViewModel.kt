package com.example.music.presentation.auth.bottomMenu.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.UserProfile
import com.example.music.data.service.AppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    private val appDatabase: AppDatabase
) : AndroidViewModel(application) {

    val language: MutableLiveData<String> = MutableLiveData("English")
    val theme: MutableLiveData<String> = MutableLiveData("light")
    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: MutableLiveData<UserProfile?> get() = _userProfile
    private val sharedPreferences = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val userProfileDao = appDatabase.userProfileDao()

    // User profile update method with database insertion
    fun updateUserProfile(username: String, imageUri: Uri?) {
        val userProfile = UserProfile(username = username, imageUri = imageUri?.toString())
        _userProfile.value = userProfile

        // Launch a coroutine to insert the user profile into the database
        viewModelScope.launch {
            try {
                userProfileDao.insertUserProfile(userProfile)
            } catch (e: Exception) {
                // Handle any error
            }
        }
    }

    // Profil yükləmə metodu
    fun loadUserProfile(id: Long) {
        viewModelScope.launch {
            try {
                // İstifadəçi profilini ID-ə görə yükləyirik
                val profile = userProfileDao.getUserProfile(id)
                _userProfile.value = profile
            } catch (e: Exception) {
                // Hata baş verdikdə buranı işlədə bilərsiniz
            }
        }
    }

    // Language and Theme preference methods
    fun setLanguage(language: String, context: Context) {
        if (this.language.value != language) {
            this.language.value = language
            saveLanguagePreference(language)
            changeAppLanguage(language, context)
        }
    }

    fun setTheme(theme: String, context: Context) {
        if (this.theme.value != theme) {
            this.theme.value = theme
            saveThemePreference(theme)
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

    private fun saveLanguagePreference(language: String) {
        if (sharedPreferences.getString("language", "") != language) {
            sharedPreferences.edit().putString("language", language).apply()
        }
    }

    private fun saveThemePreference(theme: String) {
        if (sharedPreferences.getString("theme", "") != theme) {
            sharedPreferences.edit().putString("theme", theme).apply()
        }
    }
}
