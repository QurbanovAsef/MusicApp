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
    private val viewModel: UpdatePasswordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.validationState.observe(viewLifecycleOwner) { validationState ->
            handleValidationState(validationState)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            handleLoading(isLoading)
        }

        binding?.backButtonUpdate?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding?.ContinueUP?.setOnClickListener {
            val newPassword = binding?.recoveryPassword?.text.toString()

            viewModel.updatePassword(newPassword) { success, error ->
                if (success) {
                    Toast.makeText(requireContext(), getString(R.string.password_updated_successfully), Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_updatePassword_to_successfullyRegister2)
                } else {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun handleValidationState(validationState: ValidationState) {
        binding?.recovery?.error = validationState.passwordError
        binding?.confirmation?.error = validationState.repeatPasswordError
    }

    private fun handleLoading(isLoading: Boolean) {
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding?.ContinueUP?.isEnabled = !isLoading
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
