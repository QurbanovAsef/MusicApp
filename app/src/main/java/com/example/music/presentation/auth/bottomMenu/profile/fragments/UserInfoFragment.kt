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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.databinding.FragmentUserInfoBinding
import com.example.music.utils.proileutils.ValidationUtilsProfile
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserInfoFragment : Fragment() {
    private lateinit var binding: FragmentUserInfoBinding
    private val viewModel: UserInfoViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userProfile.observe(viewLifecycleOwner) { userProfile ->
            binding.editName.setText(userProfile.firstName)
            binding.editSurName.setText(userProfile.lastName)

            if (!userProfile.imageUrl.isNullOrEmpty()) {
                Glide.with(this)
                    .load(userProfile.imageUrl)
                    .circleCrop()
                    .into(binding.profileImage)
            }
        }

        binding.profileContainer.setOnClickListener {
            pickImageFromGallery()
        }

        binding.saveButton.setOnClickListener {
            saveUserProfile()
        }

        binding.backButtonUserinfo.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun saveUserProfile() {
        val name = binding.editName.text.toString().trim()
        val surname = binding.editSurName.text.toString().trim()

        val validationState = ValidationUtilsProfile.validateProfile(name, surname)

        binding.inputName.error = validationState.nameError
        binding.inputSurname.error = validationState.surnameError

        if (!validationState.hasErrors()) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

            binding.progressBar.visibility = View.VISIBLE

            viewModel.updateUserProfile(name, surname, selectedImageUri) { success, message ->
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                if (success) findNavController().navigateUp()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.profileImage.setImageURI(selectedImageUri)
            uploadImage()
        }
    }

    private fun uploadImage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE

        viewModel.uploadProfileImage(userId, selectedImageUri!!) { success, message ->
            binding.progressBar.visibility = View.GONE
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
    }
}
