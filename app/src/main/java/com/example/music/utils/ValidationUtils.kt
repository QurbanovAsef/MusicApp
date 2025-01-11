



import android.util.Patterns
import com.example.music.utils.ValidationState
object ValidationUtils {

    // Əsas doğrulama funksiyası
    fun validate(email: String, password: String, repeatPassword: String): ValidationState {
        return ValidationState(
            emailError = validateEmail(email),
            passwordError = validatePassword(password),
            repeatPasswordError = validateRepeatPassword(password, repeatPassword)
        )
    }

    // Email doğrulaması
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "Email boş ola bilməz"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "E-poçt formatı düzgün deyil"
            else -> null
        }
    }

    // Şifrə doğrulaması
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

    // Təkrarlanan şifrə doğrulaması
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