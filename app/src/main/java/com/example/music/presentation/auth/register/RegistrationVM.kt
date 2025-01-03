package com.example.music.presentation.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.presentation.auth.core.CoreUIState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationVM @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state: MutableLiveData<CoreUIState<State>> = MutableLiveData()
    val state: LiveData<CoreUIState<State>> = _state

    fun registerUser(name: String,email: String, password: String, repeatPassword: String) {
        val validationState = ValidationUtils.validate(name,email, password, repeatPassword)

        if (validationState.hasErrors()) {
            _state.value = CoreUIState.Success(
                State(
                    isRegistered = false,
                    emailError = validationState.emailError,
                    passwordError = validationState.passwordError,
                    repeatPasswordError = validationState.repeatPasswordError
                )
            )
        } else {
            _state.value = CoreUIState.Loading(true) // Loading vəziyyəti
            registerWithFirebase(email, password)
        }
    }

    private fun registerWithFirebase(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _state.value = CoreUIState.Loading(false) // Loading bitdi
                if (task.isSuccessful) {
                    _state.value = CoreUIState.Success(
                        State(isRegistered = true, email = email, password = password)
                    )
                } else {
                    _state.value = CoreUIState.Error(null, "Qeydiyyat uğursuz oldu!")
                }
            }
            .addOnFailureListener { error ->
                _state.value = CoreUIState.Loading(false) // Loading bitdi
                _state.value = CoreUIState.Error(100, error.localizedMessage)
            }
    }

    data class State(
        val isRegistered: Boolean,
        val emailError: String? = null,
        val passwordError: String? = null,
        val repeatPasswordError: String? = null,
        val email: String? = null,
        val password: String? = null
    )
}
