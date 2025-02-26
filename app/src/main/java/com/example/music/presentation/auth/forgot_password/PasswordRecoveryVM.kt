package com.example.music.presentation.auth.forgot_password

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.music.utils.ValidationState
import com.example.music.utils.ValidationUtils


class PasswordRecoveryVM : ViewModel() {
    private val _validationState = MutableLiveData<ValidationState>()
    val validationState: LiveData<ValidationState> = _validationState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun validateEmail(email: String) {
        val emailValidation = ValidationUtils.validateEmail(email)

        _validationState.value = ValidationState(
            emailError = emailValidation
        )
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
        _isLoading.value = true

        firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val isRegistered = signInMethods?.contains("password") == true

                    if (!isRegistered) {
                        _isLoading.value = false
                        onResult(false, "Bu e-mail ilə qeydiyyat tapılmadı.")
                        return@addOnCompleteListener
                    }

                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { resetTask ->
                            _isLoading.value = false
                            if (resetTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                val errorMessage = resetTask.exception?.message ?: "Naməlum xəta"
                                Log.e("PasswordRecovery", "Şifrə sıfırlama xətası: $errorMessage")
                                onResult(false, errorMessage)
                            }
                        }
                } else {
                    val errorMessage = task.exception?.message ?: "Naməlum xəta"
                    Log.e("PasswordRecovery", "fetchSignInMethodsForEmail xətası: $errorMessage")
                    _isLoading.value = false
                    onResult(false, "Xəta baş verdi: $errorMessage")
                }
            }

    }

}
