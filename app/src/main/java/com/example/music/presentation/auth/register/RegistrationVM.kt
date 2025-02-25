package com.example.music.presentation.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.music.presentation.auth.core.CoreUIState
import com.example.music.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegistrationVM @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _state: MutableLiveData<CoreUIState<State>> = MutableLiveData()
    val state: LiveData<CoreUIState<State>> = _state

    fun registerUser(firstName: String, lastName: String, email: String, password: String, repeatPassword: String) {
        val validationState = ValidationUtils.validate(firstName, lastName, email, password, repeatPassword)

        if (validationState.hasErrors()) {
            _state.value = CoreUIState.Success(
                State(
                    isRegistered = false,
                    firstNameError = validationState.firstNameError,
                    lastNameError = validationState.lastNameError,
                    emailError = validationState.emailError,
                    passwordError = validationState.passwordError,
                    repeatPasswordError = validationState.repeatPasswordError
                )
            )
        } else {
            _state.value = CoreUIState.Loading(true)
            registerWithFirebase(firstName, lastName, email, password)
        }
    }
    private fun registerWithFirebase(firstName: String, lastName: String, email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _state.value = CoreUIState.Loading(false) // 🔥 Yükləmə bitdi
                if (task.isSuccessful) {
                    val userId = firebaseAuth.currentUser?.uid ?: return@addOnCompleteListener
                    val userData = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName
                    )

                    // 🔥 Yalnız ad və soyadı Firestore-a yaz
                    FirebaseFirestore.getInstance().collection("users").document(userId).set(userData)
                        .addOnSuccessListener {
                            _state.value = CoreUIState.Success(State(isRegistered = true))
                        }
                        .addOnFailureListener { error ->
                            _state.value = CoreUIState.Error(null, "Profil məlumatları yazıla bilmədi!")
                        }
                } else {
                    _state.value = CoreUIState.Error(null, "Qeydiyyat uğursuz oldu!")
                }
            }
            .addOnFailureListener { error ->
                _state.value = CoreUIState.Loading(false) // 🔥 Yükləmə bitdi
                _state.value = CoreUIState.Error(100, error.localizedMessage)
            }
    }


    data class State(
        val isRegistered: Boolean,
        val firstNameError: String? = null,
        val lastNameError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val repeatPasswordError: String? = null,
        val email: String? = null,
        val password: String? = null
    )
}
