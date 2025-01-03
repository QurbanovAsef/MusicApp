package com.example.music.utils

data class ValidationState(
    val nameError: String? = null, // Name üçün error sahəsi əlavə edildi
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null
) {
    fun hasErrors(): Boolean {
        return nameError != null || emailError != null || passwordError != null || repeatPasswordError != null
    }
}
