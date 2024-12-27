package com.example.music.presentation.auth.bottomMenu.home.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidprojecttest1.R


class RadioFragment : Fragment() {

    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_radio, container, false)

        // Bind UI components
        val btnPrevious: ImageButton = view.findViewById(R.id.btn_previous)
        val btnPlayPause: ImageButton = view.findViewById(R.id.btn_play_pause)
        val btnNext: ImageButton = view.findViewById(R.id.btn_next)

        // Handle button clicks
        btnPrevious.setOnClickListener {
            Toast.makeText(context, "Previous Song", Toast.LENGTH_SHORT).show()
            // Logic for skipping to the previous song
        }

        btnPlayPause.setOnClickListener {
            isPlaying = !isPlaying
            btnPlayPause.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
            val message = if (isPlaying) "Playing Song" else "Paused Song"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        btnNext.setOnClickListener {
            Toast.makeText(context, "Next Song", Toast.LENGTH_SHORT).show()
            // Logic for skipping to the next song
        }

        return view
    }
}
