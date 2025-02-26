package com.example.music.presentation.auth.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.presentation.auth.core.CoreUIState
import com.example.music.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state: MutableLiveData<CoreUIState<State>> = MutableLiveData()
    val state: LiveData<CoreUIState<State>> = _state

    fun loginUser(email: String, password: String) {
        val emailError = ValidationUtils.validateEmail(email)
        val passwordError = ValidationUtils.validatePassword(password)

        if (emailError != null || passwordError != null) {
            _state.value = CoreUIState.Success(
                State(
                    emailError = emailError,
                    passwordError = passwordError
                )
            )
        } else {
            _state.value = CoreUIState.Loading(true)
            loginWithFirebase(email, password)
        }
    }


    // Firebase ilə login etmək
    private fun loginWithFirebase(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->


                _state.value = CoreUIState.Loading(false)
                if (task.isSuccessful) {

                    _state.value = CoreUIState.Success(
                        State(
                            isLoggedIn = true,
                            email = email,
                            password = password
                        )
                    )
                } else {

                    _state.value = CoreUIState.Error(100, "Giriş uğursuz oldu!")
                }
            }
            .addOnFailureListener { error ->

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

