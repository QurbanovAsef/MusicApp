package com.example.music.presentation.auth.bottomMenu.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentProfileBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.music.activity.ContainerActivity
import java.util.Locale
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null

    // Hilt ilə `ProfileViewModel`-in təmin edilməsi
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedPreferences-dən istifadəçi ID-sini alın
        val userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getLong("user_id", -1)

        // `id`-ni ötürərək istifadəçi profilini yükləyin
        if (userId != -1L) {
            profileViewModel.loadUserProfile(userId)
        }

        // Profil məlumatlarını izləyirik
        profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                if (userProfile.imageUri != null) {
                    binding?.profileImage?.setImageURI(Uri.parse(userProfile.imageUri))
                }
                binding?.editName?.text = userProfile.username
            }
        }

        // Profil məlumatlarını redaktə etmək üçün UserInfoFragment-ə yönləndiririk
        binding?.btnEditProfile?.setOnClickListener {
            findNavController().navigate(R.id.userInfoFragment)
        }

        // Dil və tema LiveData izlənir
        profileViewModel.language.observe(viewLifecycleOwner) { language ->
            updateLanguageUI(language)
        }

        profileViewModel.theme.observe(viewLifecycleOwner) { theme ->
            updateThemeUI(theme)
        }

        // Dil seçimi üçün dialoq
        binding?.languageSection?.setOnClickListener {
            showLanguageDialog()
        }

        // Tema seçimi üçün dialoq
        binding?.themeSection?.setOnClickListener {
            showThemeDialog()
        }

        // App haqqında məlumat üçün fragmentə keçid
        binding?.aboutApp?.setOnClickListener {
            findNavController().navigate(R.id.aboutAppFragment)
        }

        // Çıxış etmək üçün dialoq
        binding?.logout?.setOnClickListener {
            showLogoutDialog()
            val activity = requireActivity() as ContainerActivity
            activity.logout()
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("English", "Azərbaycan")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setItems(languages) { _, which ->
                val selectedLanguage = languages[which]
                profileViewModel.setLanguage(selectedLanguage, requireContext())
                updateLanguageUI(selectedLanguage)
            }
            .show()
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Light Theme", "Dark Theme")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Theme")
            .setItems(themes) { _, which ->
                val selectedTheme = if (which == 0) "light" else "dark"
                profileViewModel.setTheme(selectedTheme, requireContext())
                updateThemeUI(selectedTheme)
            }
            .show()
    }

    private fun showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                val sharedPreferences = requireContext().getSharedPreferences(
                    "user_prefs", Context.MODE_PRIVATE
                )
                sharedPreferences.edit().clear().apply()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.loginFragment)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun updateLanguageUI(language: String) {
        val currentLocale = resources.configuration.locales[0]
        val newLocale = when (language) {
            "English" -> Locale("en")
            "Azərbaycan" -> Locale("az")
            else -> Locale("en")
        }

        if (currentLocale != newLocale) {
            val config = resources.configuration
            config.setLocale(newLocale)
            requireContext().createConfigurationContext(config)
            requireActivity().recreate()
        }
    }

    private fun updateThemeUI(theme: String) {
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        val newMode = if (theme == "dark") AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        if (currentMode != newMode) {
            AppCompatDelegate.setDefaultNightMode(newMode)
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
