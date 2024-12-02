package com.example.music.presentation.auth.bottomMenu.profile.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.databinding.FragmentSettingsBinding
import java.util.*

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButtonSetting.setOnClickListener {
            findNavController().popBackStack()
        }

        val sharedPreferences = requireContext().getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

        setupLanguageCheckboxes(sharedPreferences)
        setupThemeCheckboxes(sharedPreferences)
    }

    private fun setupLanguageCheckboxes(sharedPreferences: SharedPreferences) {
        val savedLanguage = sharedPreferences.getString("language", "Azerbaijani") ?: "Azerbaijani"
        binding.checkboxAzerbaijani.isChecked = savedLanguage == "Azerbaijani"
        binding.checkboxEnglish.isChecked = savedLanguage == "English"

        binding.checkboxAzerbaijani.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxEnglish.isChecked = false
                savePreference(sharedPreferences, "language", "Azerbaijani")
                changeLanguage("Azerbaijani")
            }
        }

        binding.checkboxEnglish.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxAzerbaijani.isChecked = false
                savePreference(sharedPreferences, "language", "English")
                changeLanguage("English")
            }
        }
    }

    private fun savePreference(sharedPreferences: SharedPreferences, key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun changeLanguage(language: String) {
        val locale = when (language) {
            "Azerbaijani" -> Locale("az")
            "English" -> Locale("en")
            else -> Locale.getDefault()
        }
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

        Toast.makeText(requireContext(), "Language changed to $language", Toast.LENGTH_SHORT).show()
    }

    private fun setupThemeCheckboxes(sharedPreferences: SharedPreferences) {
        // Tema seçimlərini idarə et
        binding.checkboxLight.isChecked = sharedPreferences.getString("theme", "light") == "light"
        binding.checkboxDark.isChecked = !binding.checkboxLight.isChecked

        binding.checkboxLight.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxDark.isChecked = false
                savePreference(sharedPreferences, "theme", "light")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        binding.checkboxDark.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.checkboxLight.isChecked = false
                savePreference(sharedPreferences, "theme", "dark")
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
