package com.example.music.presentation.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentSuccessfullyRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessfullyRegister : Fragment() {
    private var binding: FragmentSuccessfullyRegisterBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSuccessfullyRegisterBinding.inflate(inflater, container, false)

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.buttonText?.setOnClickListener {
            findNavController().navigate(R.id.action_successfullyRegister2_to_nav_home)
        }
    }
}