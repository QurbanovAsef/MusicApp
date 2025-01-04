package com.example.music.presentation.auth.forgot_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.utils.ValidationState

class PasswordRecoveryVM : ViewModel() {
    private val _validationState = MutableLiveData<ValidationState>()
    val validationState: LiveData<ValidationState> = _validationState

    fun validateFields(email: String, password: String, repeatPassword: String) {
        val validationResult = ValidationUtils.validate(email, password, repeatPassword)
        _validationState.value = validationResult
    }
}
