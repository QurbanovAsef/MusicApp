package com.example.music.presentation.auth.bottomMenu.home.fragmens
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentRadioBinding

class RadioFragment : Fragment() {

    private lateinit var binding: FragmentRadioBinding
    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRadioBinding.inflate(inflater, container, false)

        setupClickListeners()

        return binding.root
    }

    /**
     * Set up click listeners for buttons.
     */
    private fun setupClickListeners() {
        binding.btnPlayPause.setOnClickListener {
            isPlaying = !isPlaying
            val message = if (isPlaying) {
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
                "Playing"
            } else {
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                "Paused"
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        binding.btnPrevious.setOnClickListener {
            Toast.makeText(context, "Previous Button Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnNext.setOnClickListener {
            Toast.makeText(context, "Next Button Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
