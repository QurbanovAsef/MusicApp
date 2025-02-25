package com.example.music.utils.proileutils


data class ValidationStateProfile(
    val nameError: String? = null,
    val surnameError: String? = null
) {
    fun hasErrors(): Boolean {
        return nameError != null || surnameError != null
    }
}