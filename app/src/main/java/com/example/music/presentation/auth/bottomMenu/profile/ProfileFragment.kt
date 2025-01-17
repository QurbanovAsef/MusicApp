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
        val userId = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
