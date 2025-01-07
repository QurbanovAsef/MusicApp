package com.example.music.presentation.auth.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentRegistrationBinding
import com.example.music.presentation.auth.core.CoreUIState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Registration : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val viewModel by viewModels<RegistrationVM>()

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        _binding?.progressBar?.visibility = View.GONE
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel observer
        viewModel.state.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is CoreUIState.Success -> handleSuccess(uiState.data)
                is CoreUIState.Loading -> handleLoading(uiState.isLoading)
                is CoreUIState.Error -> handleError(uiState.errorMessage)
            }
        }

        // Error sıfırlamaq və sahələri izləmək
        binding.apply {
            TextInputLayoutEmail.error = null
            TextInputLayoutPassword.error = null
            repeatPasswordTI.error = null

            textInputEditTextEmail.doAfterTextChanged {
                TextInputLayoutEmail.error = null
            }

            textInputEditTextPassword.doAfterTextChanged {
                TextInputLayoutPassword.error = null
            }

            repeatPasswordET.doAfterTextChanged {
                repeatPasswordTI.error = null
            }

            buttonRegister.setOnClickListener { onRegisterClick() }
            singInLink.setOnClickListener {
                _binding?.apply {
                    TextInputLayoutEmail.error = null
                    TextInputLayoutPassword.error = null
                    repeatPasswordTI.error = null
                    textInputEditTextEmail.clearFocus()
                    textInputEditTextPassword.clearFocus()
                    repeatPasswordET.clearFocus()
                }
                findNavController().navigate(R.id.action_registration_to_login)
            }
        }
    }

    private fun onRegisterClick() {
        binding.apply {
            val email = textInputEditTextEmail.text.toString()
            val password = textInputEditTextPassword.text.toString()
            val repeatPassword = repeatPasswordET.text.toString()
            viewModel.registerUser(email, password, repeatPassword)
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            buttonRegister.isEnabled = !isLoading
        }
    }

    private fun handleSuccess(state: RegistrationVM.State) {
        if (state.isRegistered) {
            Toast.makeText(requireContext(), "Qeydiyyat uğurlu!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_registration_to_successfullyRegister2)
        } else {
            binding.apply {
                TextInputLayoutEmail.error = state.emailError
                TextInputLayoutPassword.error = state.passwordError
                repeatPasswordTI.error = state.repeatPasswordError
            }
        }
    }

    private fun handleError(errorMessage: String?) {
        Toast.makeText(requireContext(), "Xəta: $errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}