package com.example.music.presentation.auth.forgot_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.example.music.utils.ValidationState
import com.example.music.utils.ValidationUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdatePasswordViewModel : ViewModel() {

    private val _validationState = MutableLiveData<ValidationState>()
    val validationState: LiveData<ValidationState> get() = _validationState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun validatePasswords(password: String, confirmPassword: String) {
        val passwordValidation = ValidationUtils.validatePassword(password)
        val confirmPasswordValidation = ValidationUtils.validateRepeatPassword(password, confirmPassword)

        _validationState.value = ValidationState(
            passwordError = passwordValidation,
            repeatPasswordError = confirmPasswordValidation
        )
    }

    fun updatePassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            _isLoading.value = true

            user.reload().addOnCompleteListener { reloadTask ->
                if (reloadTask.isSuccessful) {
                    val oldPassword = user.email // 🔥 Yeni şifrə ilə eyni olub-olmadığını yoxlayırıq.

                    if (newPassword == oldPassword) {
                        _isLoading.value = false
                        onResult(false, "Bu şifrə artıq mövcuddur") // ❌ Eyni şifrədirsə sadəcə Toast çıxır.
                        return@addOnCompleteListener
                    }

                    user.updatePassword(newPassword).addOnCompleteListener { updateTask ->
                        viewModelScope.launch {
                            delay(2000)
                            _isLoading.value = false
                        }
                        if (updateTask.isSuccessful) {
                            onResult(true, null) // ✅ Şifrə uğurla yeniləndi
                        } else {
                            onResult(false, updateTask.exception?.message ?: "Şifrə dəyişdirilmədi.")
                        }
                    }
                } else {
                    _isLoading.value = false
                    onResult(false, "Xəta baş verdi")
                }
            }
        } else {
            onResult(false, "Xəta baş verdi")
        }
    }
}

