package com.example.music.utils

data class ValidationState(
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null
) {
    fun hasErrors(): Boolean {
        return firstNameError != null || lastNameError != null || emailError != null || passwordError != null || repeatPasswordError != null
    }
}
