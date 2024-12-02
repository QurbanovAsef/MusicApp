package com.example.music.presentation.auth.forgot_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.utils.ValidationState

class UpdatePasswordViewModel : ViewModel() {

    private val _validationState = MutableLiveData<ValidationState>()
    val validationState: LiveData<ValidationState> get() = _validationState

    // Şifrə və təkrarlanan şifrəni yoxlama funksiyası
    fun validatePasswords(password: String, confirmPassword: String) {
        val passwordValidation = ValidationUtils.validatePassword(password)
        val confirmPasswordValidation = ValidationUtils.validateRepeatPassword(password, confirmPassword)

        _validationState.value = ValidationState(
            passwordError = passwordValidation,
            repeatPasswordError = confirmPasswordValidation
        )
    }
}
