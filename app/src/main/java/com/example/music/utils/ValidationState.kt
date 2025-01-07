package com.example.music.utils

data class ValidationState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val repeatPasswordError: String? = null
) {
    // Hər hansı bir səhv olub-olmamasını yoxlayırıq
    fun hasErrors(): Boolean {
        return emailError != null || passwordError != null || repeatPasswordError != null
    }
}