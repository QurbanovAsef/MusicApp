package com.example.music.presentation.auth.bottomMenu.profile.fragments

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.music.data.model.response.UserProfile
import com.example.music.data.service.AppDatabase
import com.example.music.utils.proileutils.ValidationStateProfile
import com.example.music.utils.proileutils.ValidationUtilsProfile
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val appDatabase: AppDatabase
) : ViewModel() {
    private val _validationState = MutableLiveData<ValidationStateProfile>()
    val validationState: LiveData<ValidationStateProfile> get() = _validationState

    private val _profileUpdateStatus = MutableLiveData<Boolean>()
    val profileUpdateStatus: LiveData<Boolean> get() = _profileUpdateStatus

    private val _profileImageUri = MutableLiveData<Uri?>()
    val profileImageUri: LiveData<Uri?> get() = _profileImageUri

    private val userProfileDao = appDatabase.userProfileDao() // Get the UserProfileDao from AppDatabase

    fun validateInputs(name: String) {
        val validationResult = ValidationUtilsProfile.validateProfile(name)
        _validationState.value = validationResult
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val profile = userProfileDao.getAllUserProfiles().value?.firstOrNull()
                profile?.let {
                    _profileImageUri.value = Uri.parse(it.imageUri)
                }
            } catch (e: Exception) {
                _profileUpdateStatus.postValue(false)
            }
        }
    }

    fun updateUserProfile(name: String, imageUri: Uri?) {
        val validationResult = ValidationUtilsProfile.validateProfile(name)
        _validationState.postValue(validationResult)

        if (validationResult.hasErrorsProfile()) {
            return
        }
        viewModelScope.launch {
            try {
                val existingUser = userProfileDao.getAllUserProfiles().value?.firstOrNull()
                if (existingUser != null) {
                    // Mövcud profili yenilə
                    existingUser.username = name
                    existingUser.imageUri = imageUri?.toString()
                    userProfileDao.updateUserProfile(existingUser)
                } else {
                    // Yeni profil əlavə et
                    val newUser = UserProfile(username = name, imageUri = imageUri?.toString())
                    userProfileDao.insertUserProfile(newUser)
                }
                _profileUpdateStatus.postValue(true)
            } catch (e: Exception) {
                _profileUpdateStatus.postValue(false)
            }
        }
    }
}
