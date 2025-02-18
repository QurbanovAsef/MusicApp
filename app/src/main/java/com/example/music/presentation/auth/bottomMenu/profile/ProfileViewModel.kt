package com.example.music.presentation.auth.bottomMenu.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.UserProfile
import com.example.music.data.service.AppDatabase
import com.example.music.utils.AppConst.LANG_KEY_DEFAULT
import com.example.music.utils.AppConst.LANG_KEY_LANGUAGE
import com.example.music.utils.AppConst.SHARED_KEY_PREFERENCES
import com.example.music.utils.AppConst.SHARED_KEY_THEME
import com.example.music.utils.AppConst.THEME_KEY_DARK
import com.example.music.utils.AppConst.THEME_KEY_LIGHT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    application: Application,
    appDatabase: AppDatabase
) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences(SHARED_KEY_PREFERENCES, Context.MODE_PRIVATE)

    val language: MutableLiveData<String> =
        MutableLiveData(sharedPreferences.getString(LANG_KEY_LANGUAGE, LANG_KEY_DEFAULT))

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> get() = _userProfile

    private val userProfileDao = appDatabase.userProfileDao()

    // Profil məlumatlarını yeniləyir
    fun updateUserProfile(username: String, imageUri: Uri?) {
        val userProfile = UserProfile(username = username, imageUri = imageUri?.toString())
        _userProfile.value = userProfile

        viewModelScope.launch {
            try {
                // Yeni istifadəçi profilini verilənlər bazasına əlavə edir
                userProfileDao.insertUserProfile(userProfile)
            } catch (e: Exception) {
                // Xətanı idarə edin
            }
        }
    }

    // Profil məlumatlarını yükləyir
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                // Verilənlər bazasından istifadəçi profilini yükləyirik
                val profile = userProfileDao.getAllUserProfiles().value?.firstOrNull() // İlk profili alırıq
                _userProfile.value = profile
            } catch (e: Exception) {
                // Xətalarla əlaqəli əməliyyatları buraya əlavə edin
                _userProfile.value = null
            }
        }
    }

    // Dil və Tema dəyişikliklərini saxlayır
    fun setLanguage(language: String) {
        if (this.language.value != language) {
            saveLanguagePreference(language)
            this.language.value = language
        }
    }

    fun setTheme(theme: String) {
        saveThemePreference(theme)
        changeAppTheme(theme)
    }

    private fun changeAppTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(
            if (theme == THEME_KEY_DARK) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun saveLanguagePreference(language: String) {
        if (sharedPreferences.getString(LANG_KEY_LANGUAGE, LANG_KEY_DEFAULT) != language) {
            sharedPreferences.edit().putString(LANG_KEY_LANGUAGE, language).apply()
        }
    }

    private fun saveThemePreference(theme: String) {
        if (sharedPreferences.getString(SHARED_KEY_THEME, THEME_KEY_LIGHT) != theme) {
            sharedPreferences.edit().putString(SHARED_KEY_THEME, theme).apply()
        }
    }
}
