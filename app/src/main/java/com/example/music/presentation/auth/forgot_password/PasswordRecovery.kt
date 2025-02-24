package com.example.music.presentation.auth.forgot_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentPasswordRecoveryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PasswordRecovery : Fragment() {
    private var binding: FragmentPasswordRecoveryBinding? = null
    private val passwordViewModel: PasswordRecoveryVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordRecoveryBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        passwordViewModel.validationState.observe(viewLifecycleOwner) { validationState ->
            binding?.EmailRecovery?.error = validationState.emailError
        }

        binding?.backButton?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding?.Continue?.setOnClickListener {
            val email = binding?.textInputEditTextEmail?.text.toString()

            passwordViewModel.validateEmail(email)

            if (passwordViewModel.validationState.value?.hasErrors() == false) {
                passwordViewModel.sendPasswordResetEmail(email) { success, error ->
                    if (success) {
                        Toast.makeText(requireContext(), "Şifrə sıfırlama e-poçtu göndərildi!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_passwordRecovery_to_updatePassword)
                    } else {
                        Toast.makeText(requireContext(), "Xəta: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
