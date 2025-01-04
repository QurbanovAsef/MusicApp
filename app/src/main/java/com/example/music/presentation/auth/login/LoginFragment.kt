package com.example.music.presentation.auth.login

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
import com.example.androidprojecttest1.databinding.FragmentLoginBinding
import com.example.music.presentation.auth.core.CoreUIState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val viewModel by viewModels<LoginVM>()
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        _binding?.progressBar?.visibility = View.GONE
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // SharedPreferences ilə istifadəçi girişini yoxlamaq
        val sharedPreferences = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false)

        if (isLoggedIn) {
            findNavController().navigate(R.id.nav_home) // HomeFragment-ə keçid
        }

        // Sahələr və düymələr
        binding.apply {
            TextInputLayoutEmail.error = null
            TextInputLayoutPassword.error = null

            textInputEditTextEmail.doAfterTextChanged {
                TextInputLayoutEmail.error = null
            }
            textInputEditTextPassword.doAfterTextChanged {
                TextInputLayoutPassword.error = null
            }

            buttonSingIn.setOnClickListener {
                val email = textInputEditTextEmail.text.toString()
                val password = textInputEditTextPassword.text.toString()
                viewModel.loginUser(email, password)
            }

            registerLink.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_registration)
            }

            forgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_passwordRecovery)
            }
        }

        // ViewModel observer
        viewModel.state.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {
                is CoreUIState.Success -> handleSuccess(uiState.data)
                is CoreUIState.Loading -> handleLoading(uiState.isLoading)
                is CoreUIState.Error -> handleError(uiState.errorMessage)
            }
        }
    }

    private fun handleSuccess(state: LoginVM.State) {
        if (state.isLoggedIn) {
            // SharedPreferences-də giriş məlumatını qeyd etmək
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("is_logged_in", true).apply()

            Toast.makeText(requireContext(), "Giriş Uğurlu", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_loginFragment_to_successfullyRegister2)
        } else {
            binding.apply {
                TextInputLayoutEmail.error = state.emailError
                TextInputLayoutPassword.error = state.passwordError
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            buttonSingIn.isEnabled = !isLoading
        }
    }

    private fun handleError(errorMessage: String?) {
        Toast.makeText(requireContext(), "Xəta: $errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        _binding?.apply {
            TextInputLayoutEmail.error = null
            TextInputLayoutPassword.error = null
            textInputEditTextEmail.clearFocus()
            textInputEditTextPassword.clearFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}