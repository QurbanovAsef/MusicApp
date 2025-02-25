package com.example.music.utils.proileutils

object ValidationUtilsProfile {

    fun validateProfile(name: String, surname: String): ValidationStateProfile {
        return ValidationStateProfile(
            nameError = validateName(name),
            surnameError = validateSurname(surname)
        )
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "Ad boş ola bilməz"
            name.length < 3 -> "Ad ən azı 3 simvol olmalıdır"
            !name.all { it.isLetter() || it.isWhitespace() } -> "Ad yalnız hərflərdən ibarət olmalıdır"
            else -> null
        }
    }

    private fun validateSurname(surname: String): String? {
        return when {
            surname.isBlank() -> "Soyad boş ola bilməz"
            surname.length < 3 -> "Soyad ən azı 3 simvol olmalıdır"
            !surname.all { it.isLetter() || it.isWhitespace() } -> "Soyad yalnız hərflərdən ibarət olmalıdır"
            else -> null
        }
    }
}