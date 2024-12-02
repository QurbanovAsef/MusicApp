package com.example.music.presentation.auth.bottomMenu.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentProfileBinding
import com.example.music.activity.ContainerActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // profileHeader-ə klik edildikdə UserInfoFragment-ə keçid
        binding.profileHeader.setOnClickListener {
            findNavController().navigate(R.id.userInfoFragment)
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }
        binding.btnAboutApp.setOnClickListener {
            findNavController().navigate(R.id.aboutAppFragment)
        }

        // Logout düyməsi
        binding.btnLogout.setOnClickListener {
            val sharedPreferences = requireContext().getSharedPreferences(
                "user_prefs", android.content.Context.MODE_PRIVATE
            )
            sharedPreferences.edit().clear().apply()

            // Logout metodunu ContainerActivity-də çağırırıq
            (requireActivity() as? ContainerActivity)?.logout()
        }

        binding.btnAboutApp.setOnClickListener {
            findNavController().navigate(R.id.aboutAppFragment)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
