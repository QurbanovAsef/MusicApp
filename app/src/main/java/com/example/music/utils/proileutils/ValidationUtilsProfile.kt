package com.example.music.utils.proileutils


object ValidationUtilsProfile {

    fun validateProfile(name: String): ValidationStateProfile {
        return ValidationStateProfile(
            nameErrorProfile = validateNameProfile(name),
        )
    }

    private fun validateNameProfile(name: String): String? {
        return when {
            name.isBlank() -> "Ad boş ola bilməz"
            name.length < 3 -> "Ad ən azı 3 simvol olmalıdır"
            !name.all { it.isLetter() || it.isWhitespace() } -> "Ad yalnız hərflərdən ibarət olmalıdır"
            else -> null
        }
    }
}
