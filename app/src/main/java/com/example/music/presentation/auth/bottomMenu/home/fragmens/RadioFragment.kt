package com.example.music.presentation.auth.bottomMenu.home.fragmens

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.fragment.app.Fragment

import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentRadioBinding

import com.example.music.data.model.response.RadioStationResponse
import com.example.music.data.retrofit.RadioRetrofitInstance


class RadioFragment : Fragment() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentStationIndex = 0
    private var radioStations: List<RadioStationResponse> = listOf()
    private lateinit var binding: FragmentRadioBinding
    private var isPlaying = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRadioBinding.inflate(inflater, container, false)
        val root = binding.root

        // Fetch radio stations from the API
//        fetchRadioStations()

        // Handle Play/Pause button click
        binding.btnPlayPause.setOnClickListener {
            if (isPlaying) {
                pauseStream()
                binding.btnPlayPause.setImageResource(R.drawable.ic_play) // Play icon
            } else {
                playStream()
                binding.btnPlayPause.setImageResource(R.drawable.ic_pause) // Pause icon
            }
            isPlaying = !isPlaying
        }

        // Handle Previous button click
        binding.btnPrevious.setOnClickListener {
            if (radioStations.isNotEmpty()) {
                currentStationIndex = (currentStationIndex - 1 + radioStations.size) % radioStations.size
                playStream()
            }
        }

        // Handle Next button click
        binding.btnNext.setOnClickListener {
            if (radioStations.isNotEmpty()) {
                currentStationIndex = (currentStationIndex + 1) % radioStations.size
                playStream()
            }
        }

        return root
    }

//    private fun fetchRadioStations() {
//        RadioRetrofitInstance.api.getRadioStations().enqueue(object : retrofit2.Callback<List<RadioStationResponse>> {
//            override fun onResponse(
//                call: retrofit2.Call<List<RadioStationResponse>>,
//                response: retrofit2.Response<List<RadioStationResponse>>
//            ) {
//                if (response.isSuccessful) {
//                    radioStations = response.body() ?: listOf()
//                    if (radioStations.isNotEmpty()) {
//                        playStream()
//                    }
//                } else {
//                    Toast.makeText(context, "Failed to load radio stations", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: retrofit2.Call<List<RadioStationResponse>>, t: Throwable) {
//                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

    private fun playStream() {
        if (radioStations.isNotEmpty()) {
            val station = radioStations[currentStationIndex]
            val streamUrl = station.streamUrl
            mediaPlayer?.apply {
                reset()
                setDataSource(streamUrl)
                prepareAsync()
                setOnPreparedListener {
                    start()
                    this@RadioFragment.isPlaying = true
                    Toast.makeText(context, "Now Playing: ${station.name}", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(streamUrl)
                    setOnPreparedListener {
                        start()
                        this@RadioFragment.isPlaying = true
                        Toast.makeText(context, "Now Playing: ${station.name}", Toast.LENGTH_SHORT).show()
                    }
                    prepareAsync()
                }
            }
        }
    }

    private fun pauseStream() {
        mediaPlayer?.apply {
            pause()
            this@RadioFragment.isPlaying = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
    }
}
