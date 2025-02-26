package com.example.music.presentation.auth.bottomMenu.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentProfileBinding
import com.example.music.activity.ContainerActivity
import com.example.music.utils.AppConst.LANG_KEY_AZ
import com.example.music.utils.AppConst.LANG_KEY_EN
import com.example.music.utils.AppConst.SHARED_KEY_PREFERENCES
import com.example.music.utils.AppConst.THEME_KEY_DARK
import com.example.music.utils.AppConst.THEME_KEY_LIGHT
import com.example.music.utils.LocaleUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE

        profileViewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            userProfile?.let {
                binding.editName.text = "${it.firstName} ${it.lastName}"

                if (!it.imageUrl.isNullOrEmpty()) {
                    binding.progressBar.visibility = View.VISIBLE
                    Glide.with(this)
                        .load(Uri.parse(it.imageUrl))
                        .circleCrop()
                        .into(binding.profileImage1)
                    binding.progressBar.visibility = View.GONE
                }
            }
            binding.progressBar.visibility = View.GONE
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.userInfoFragment)
        }
        binding.languageSection.setOnClickListener { showLanguageDialog() }
        binding.themeSection.setOnClickListener { showThemeDialog() }
        binding.aboutApp.setOnClickListener { findNavController().navigate(R.id.aboutAppFragment) }
        binding.logout.setOnClickListener { showLogoutDialog() }
    }

    private fun showLanguageDialog() {
        val context = requireContext()
        val languages = hashMapOf(
            LANG_KEY_EN to context.getString(R.string.language_english),
            LANG_KEY_AZ to context.getString(R.string.language_azerbaijani)
        )

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.select_language))
            .setItems(languages.values.toTypedArray()) { _, which ->
                val selectedLanguage = languages.keys.toList()[which]
                profileViewModel.setLanguage(selectedLanguage)
                LocaleUtil.applyLocalizedContext(context, selectedLanguage)
                refreshFragment()
                requireActivity().recreate()
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
    }

    private fun showThemeDialog() {
        val context = requireContext()
        val themes = arrayOf(context.getString(R.string.light_theme), context.getString(R.string.dark_theme))

        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.select_theme))
            .setItems(themes) { _, which ->
                val selectedTheme = if (which == 0) THEME_KEY_LIGHT else THEME_KEY_DARK
                profileViewModel.setTheme(selectedTheme)
                refreshFragment()
            }
            .setNegativeButton(context.getString(R.string.cancel), null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
    }

    private fun showLogoutDialog() {
        val context = requireContext()
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.logout))
            .setMessage(context.getString(R.string.logout_confirmation))
            .setPositiveButton(context.getString(R.string.yes)) { _, _ ->
                val sharedPreferences = context.getSharedPreferences(SHARED_KEY_PREFERENCES, Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()
                Toast.makeText(context, context.getString(R.string.successful_logout), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.loginFragment)
            }
            .setNegativeButton(context.getString(R.string.no), null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GRAY)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
    }

    private fun refreshFragment() {
        parentFragmentManager.beginTransaction().detach(this).attach(this).commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
