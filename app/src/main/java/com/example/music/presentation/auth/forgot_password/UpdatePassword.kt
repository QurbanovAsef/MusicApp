package com.example.music.presentation.auth.forgot_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentUpdatePasswordBinding
import com.example.music.utils.ValidationState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePassword : Fragment() {
    private var binding: FragmentUpdatePasswordBinding? = null
    private val viewModel: UpdatePasswordViewModel by activityViewModels() // ViewModel-in əldə edilməsi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel-dən validation vəziyyətini müşahidə edirik
        viewModel.validationState.observe(viewLifecycleOwner) { validationState ->
            handleValidationState(validationState)
        }

        binding?.backButtonUpdate?.setOnClickListener {
            findNavController().popBackStack()
            Toast.makeText(requireContext(), "Geri qayıt", Toast.LENGTH_SHORT).show()
        }

        binding?.ContinueUP?.setOnClickListener {
            val password = binding?.recoveryPassword?.text.toString()
            val confirmPassword = binding?.confirmationPassword?.text.toString()

            // Validasiya prosesi
            viewModel.validatePasswords(password, confirmPassword)

            // Validasiya nəticəsini yoxlayırıq
            viewModel.validationState.observe(viewLifecycleOwner) { validationState ->
                if (!validationState.hasErrors()) {
                    findNavController().navigate(R.id.action_updatePassword_to_successfullyRegister2)
                } else {
                    // Xətalar varsa, UI-ni yeniləyirik
                    binding?.recovery?.error = validationState.passwordError
                    binding?.confirmation?.error = validationState.repeatPasswordError
                }
            }
        }
    }

    private fun handleValidationState(validationState: ValidationState) {
        binding?.recovery?.error = validationState.passwordError
        binding?.confirmation?.error = validationState.repeatPasswordError
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
