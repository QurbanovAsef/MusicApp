package com.example.music.utils

import android.util.Patterns

object ValidationUtils {

    // Validation state'in yaradılmasında istifadə ediləcək metodlar public olacaq
    fun validate(firstName: String, lastName: String, email: String, password: String, repeatPassword: String): ValidationState {
        return ValidationState(
            firstNameError = validateFirstName(firstName),
            lastNameError = validateLastName(lastName),
            emailError = validateEmail(email),
            passwordError = validatePassword(password),
            repeatPasswordError = validateRepeatPassword(password, repeatPassword)
        )
    }


    fun validateFirstName(firstName: String): String? {
        return if (firstName.isBlank()) "Ad boş ola bilməz" else null
    }

    fun validateLastName(lastName: String): String? {
        return if (lastName.isBlank()) "Soyad boş ola bilməz" else null
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email boş ola bilməz"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-poçt formatı düzgün deyil"
            else -> null
        }
    }
    fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "Şifrə boş ola bilməz"
            password.length < 8 -> "Şifrə ən azı 8 simvol olmalıdır"
            !password.any { it.isUpperCase() } -> "Şifrədə ən azı bir böyük hərf olmalıdır"
            !password.any { it.isLowerCase() } -> "Şifrədə ən azı bir kiçik hərf olmalıdır"
            !password.any { it.isDigit() } -> "Şifrədə ən azı bir rəqəm olmalıdır"
            else -> null
        }
    }

    fun validateRepeatPassword(password: String, repeatPassword: String): String? {
        return if (repeatPassword.isBlank()) {
            "Təkrarlanan şifrə boş ola bilməz"
        } else if (repeatPassword != password) {
            "Şifrələr uyğun gəlmir"
        } else {
            null
        }
    }
}
