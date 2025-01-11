package com.example.music.presentation.auth.bottomMenu.profile.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentUserInfoBinding

import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UserInfoFragment : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserInfoViewModel by viewModels()

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                uri?.let {
                    // Şəkilin formatını və ölçüsünü yoxlayırıq
                    if (validateImage(it)) {
                        viewModel.setImageUri(it) // Şəkil URI-si ViewModel-ə təyin olunur
                        binding.profileImage.setImageURI(it) // Şəkil göstərilir
                        binding.progressBar.visibility = View.VISIBLE // ProgressBar göstərilir
                    } else {
                        // Şəkil formatı və ya ölçüsü düzgün deyil, xəbərdarlıq göstəririk
                        Toast.makeText(requireContext(), "Şəkil formatı və ya ölçüsü düzgün deyil", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun validateImage(uri: Uri): Boolean {
        val file = File(uri.path!!)
        val maxSizeInMB = 2 // MB
        val allowedFormats = listOf("image/jpeg", "image/png")

        val fileSizeInMB = file.length() / (1024 * 1024)
        val fileFormat = requireContext().contentResolver.getType(uri)

        return fileSizeInMB <= maxSizeInMB && allowedFormats.contains(fileFormat)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonUserinfo.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            val name = binding.editName.text.toString()

            viewModel.validateInputs(name)

            viewModel.validationState.observe(viewLifecycleOwner) { validationState ->
                if (!validationState.hasErrorsProfile()) {
                    viewModel.updateUserProfile(name, viewModel.profileImageUri.value)
                } else {
                    binding.inputName.error = validationState.nameErrorProfile
                }
            }
        }

        viewModel.profileUpdateStatus.observe(viewLifecycleOwner) { success ->
            binding.progressBar.visibility = View.GONE
            if (success) {
                Toast.makeText(requireContext(), "Məlumatlar uğurla yeniləndi", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.profileFragment) // Profilə keçid
            } else {
                Toast.makeText(requireContext(), "Xəta baş verdi", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
