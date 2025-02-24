package com.example.music.presentation.auth.forgot_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.music.utils.ValidationState
import com.example.music.utils.ValidationUtils

class PasswordRecoveryVM : ViewModel() {
    private val _validationState = MutableLiveData<ValidationState>()
    val validationState: LiveData<ValidationState> = _validationState

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun validateEmail(email: String) {
        val emailValidation = ValidationUtils.validateEmail(email)

        _validationState.value = ValidationState(
            emailError = emailValidation
        )
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message ?: "Şifrə sıfırlama e-poçtu göndərilə bilmədi.")
                }
            }
    }




}
