package com.example.music.presentation.auth.bottomMenu.profile.fragments

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.data.model.response.UserProfile
import com.example.music.data.service.AppDatabase
import com.example.music.utils.proileutils.ValidationStateProfile
import com.example.music.utils.proileutils.ValidationUtilsProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    fun updateUserProfile(name: String, imageUri: Uri?) {
        val userProfile = UserProfile(username = name, imageUri = imageUri?.toString())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                userProfileDao.insertUserProfile(userProfile)  // Insert the user profile into the database
                _profileUpdateStatus.postValue(true)
            } catch (e: Exception) {
                _profileUpdateStatus.postValue(false)
            }
        }
    }

    fun setImageUri(uri: Uri) {
        _profileImageUri.value = uri
    }
}
