package com.example.music.presentation.auth.bottomMenu.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentProfileBinding
import com.example.music.activity.ContainerActivity
import com.example.music.utils.AppConst.LANG_KEY_AZ
import com.example.music.utils.AppConst.LANG_KEY_EN
import com.example.music.utils.AppConst.SHARED_KEY_PREFERENCES
import com.example.music.utils.AppConst.THEME_KEY_DARK
import com.example.music.utils.AppConst.THEME_KEY_LIGHT
import com.example.music.utils.LocaleUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var binding: FragmentProfileBinding? = null
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // İstifadəçi ID-ni əldə edirik
        val userId = requireContext().getSharedPreferences(SHARED_KEY_PREFERENCES, Context.MODE_PRIVATE)
            .getLong("user_id", -1)

        if (userId != -1L) {
            profileViewModel.loadUserProfile() // ID istifadə edilməyəcək
        }


        profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                // Profil şəkli və adı yenilə
                binding?.profileImage1?.setImageURI(Uri.parse(it.imageUri))
                binding?.editName?.text = it.username
            }
        }

        binding?.btnEditProfile?.setOnClickListener {
            findNavController().navigate(R.id.userInfoFragment)  // Profil məlumatlarını redaktə etməyə getmək
        }

        // Dil və tema LiveData izlənir
        profileViewModel.language.observe(viewLifecycleOwner) { language ->
            updateLanguageUI(language)
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
        val languages = hashMapOf(LANG_KEY_EN to "English", LANG_KEY_AZ to "Azərbaycan")

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Language")
            .setItems(languages.values.toTypedArray()) { _, which ->
                val selectedLanguage = languages.entries.toList()[which].key
                profileViewModel.setLanguage(selectedLanguage)
            }
            .show()
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Light Theme", "Dark Theme")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Select Theme")
            .setItems(themes) { _, which ->
                val selectedTheme = if (which == 0) THEME_KEY_LIGHT else THEME_KEY_DARK
                profileViewModel.setTheme(selectedTheme)
            }
            .show()
    }

    private fun showLogoutDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                val sharedPreferences = requireContext().getSharedPreferences(
                    SHARED_KEY_PREFERENCES, Context.MODE_PRIVATE
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

        if (currentLocale.language != language) {
            LocaleUtil.applyLocalizedContext(requireContext(), language)
            requireActivity().recreate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
