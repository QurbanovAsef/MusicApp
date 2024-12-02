package com.example.music.presentation.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.presentation.auth.core.CoreUIState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val firebaseAuth: FirebaseAuth // FirebaseAuth burada inject edilir
) : ViewModel() {

    private val _state: MutableLiveData<CoreUIState<State>> = MutableLiveData()
    val state: LiveData<CoreUIState<State>> = _state

    // Login funksiyasının işləyişi
    fun loginUser(email: String, password: String) {
        // Validation üçün ValidationUtils istifadə edilir
        val validationState = ValidationUtils.validate(email, password, password)

        if (validationState.hasErrors()) {
            Log.d("DDDDDDDDD", validationState.toString())

            // Əgər validation xətası varsa, error məlumatı göndərilir
            _state.value = CoreUIState.Success(
                State(
                    emailError = validationState.emailError,
                    passwordError = validationState.passwordError
                )
            )
        } else {
            // Validation düzgün oldusa, loading vəziyyətini göstəririk
            _state.value = CoreUIState.Loading(true)
            // Firebase ilə login etmək
            loginWithFirebase(email, password)
        }
    }

    // Firebase ilə login etmək
    private fun loginWithFirebase(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                Log.d("DDDDDDDDD", task.toString())

                // Loading vəziyyətini qapatırıq
                _state.value = CoreUIState.Loading(false)
                if (task.isSuccessful) {
                    // Əgər login uğurludursa, success mesajı göndəririk
                    _state.value = CoreUIState.Success(
                        State(
                            isLoggedIn = true,
                            email = email,
                            password = password
                        )
                    )
                } else {
                    // Əgər login uğursuz oldusa, error mesajı göndəririk
                    _state.value = CoreUIState.Error(100, "Giriş uğursuz oldu!")
                }
            }
            .addOnFailureListener { error ->

                Log.d("DDDDDDDDD", error.toString())


                // Firebase ilə əlaqə kəsilərsə, error mesajı göstəririk
                _state.value = CoreUIState.Loading(false)
                _state.value = CoreUIState.Error(100, error.localizedMessage ?: "Xəta baş verdi!")
            }
    }

    data class State(
        val email: String? = null,
        val password: String? = null,
        val isLoggedIn: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null
    )
}

//Asef123!!