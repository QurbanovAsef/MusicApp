package com.example.music.presentation.auth.bottomMenu.profile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentUserInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserInfoFragment : Fragment() {

    private var _binding: FragmentUserInfoBinding? = null
    private val binding get() = _binding!!

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

        // Yadda saxla düyməsinə klik
        binding.saveButton.setOnClickListener {
            val name = binding.editName.text.toString()
            val email = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()

            if (validateInputs(name, email, password)) {
                // Firebase ilə məlumatları yenilə
                updateFirebaseUserInfo(name, email, password)
            }
        }
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        // Əsas yoxlama məntiqi
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Bütün sahələri doldurun!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun updateFirebaseUserInfo(name: String, email: String, password: String) {
        val user = FirebaseAuth.getInstance().currentUser

        // Əgər istifadəçi varsa, məlumatları Firebase ilə yeniləyirik
        user?.let {
            // Email və şifrə yeniləməsi
            it.updateEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Email yeniləndi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Email yenilənmədi", Toast.LENGTH_SHORT).show()
                }
            }

            it.updatePassword(password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Şifrə yeniləndi", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Şifrə yenilənmədi", Toast.LENGTH_SHORT).show()
                }
            }

            // Firebase Realtime Database-də istifadəçi məlumatlarını saxlayırıq
            val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
            val userId = it.uid

            val userMap = mapOf(
                "username" to name,
                "email" to email
                // Burada şəkil URL-sini əlavə etmirik, çünki şəkil seçmək funksiyasını ləğv etdik
            )

            databaseRef.child(userId).setValue(userMap)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Məlumatlar uğurla saxlanıldı", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Xəta baş verdi", Toast.LENGTH_SHORT).show()
                }
        }
    }
}