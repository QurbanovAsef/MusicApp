package com.example.music.presentation.auth.bottomMenu.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentProfileBinding
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.music.activity.ContainerActivity
import java.util.Locale

class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        // Dil və tema LiveData izlənir
        profileViewModel.language.observe(viewLifecycleOwner) { language ->
            // Dil dəyişdikdə UI-ni yeniləyirik
            updateLanguageUI(language)
        }

        profileViewModel.theme.observe(viewLifecycleOwner) { theme ->
            // Tema dəyişdikdə UI-ni yeniləyirik
            updateThemeUI(theme)
        }

        binding?.btnEditProfile?.setOnClickListener {
            findNavController().navigate(R.id.userInfoFragment)
        }

        binding?.languageSection?.setOnClickListener {
            showLanguageDialog()
        }

        binding?.themeSection?.setOnClickListener {
            showThemeDialog()
        }

        binding?.aboutApp?.setOnClickListener {
            findNavController().navigate(R.id.aboutAppFragment)
        }

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
