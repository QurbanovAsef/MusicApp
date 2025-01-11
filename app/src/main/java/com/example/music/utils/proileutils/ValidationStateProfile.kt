package com.example.music.utils.proileutils

data class ValidationStateProfile(
    val nameErrorProfile: String? = null,
) {
    fun hasErrorsProfile(): Boolean {
        return nameErrorProfile != null
    }
}
