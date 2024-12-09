package com.example.music.presentation.auth.bottomMenu.music

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentMusicBinding
import com.example.music.data.model.response.PlaylistItem
import com.example.music.data.model.response.Show
import com.example.music.data.model.response.ShowsResponse
import com.example.music.presentation.viewmodel.SharedViewModel

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isLiked = false
    private var currentSong: PlaylistItem? = null
    private var songsList: List<PlaylistItem> = listOf()
    private var currentSongIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Göstərilən "show" məlumatı və ya seçilən mahnı məlumatı alınır
        val show = arguments?.getParcelable<Show>("show")
        currentSong = arguments?.getParcelable("song")

        // API-dən mahnılar alınır
        sharedViewModel.getAllSongs()

        sharedViewModel.allSongs.observe(viewLifecycleOwner) { songs ->
            songsList = songs
            if (currentSong == null && songs.isNotEmpty()) {
                currentSong = songs[0]
                currentSongIndex = 0
            } else {
                currentSongIndex = songsList.indexOf(currentSong)
            }
            currentSong?.let {
                updateUI(it)
                setupMediaPlayer(it)
            }
        }

        // Play/Pause düyməsi funksionallığı
        binding.playPauseButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer?.pause()
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
                Toast.makeText(requireContext(), "Paused", Toast.LENGTH_SHORT).show()
            } else {
                mediaPlayer?.start()
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
                Toast.makeText(requireContext(), "Playing", Toast.LENGTH_SHORT).show()
            }
            isPlaying = !isPlaying
        }

        // Geri düyməsi
        binding.rewindButton.setOnClickListener {
            if (currentSongIndex > 0) {
                currentSongIndex--
                playSongAt(currentSongIndex)
            } else {
                Toast.makeText(requireContext(), "No previous song", Toast.LENGTH_SHORT).show()
            }
        }

        // İrəli düyməsi
        binding.forwardButton.setOnClickListener {
            if (currentSongIndex < songsList.size - 1) {
                currentSongIndex++
                playSongAt(currentSongIndex)
            } else {
                Toast.makeText(requireContext(), "No next song", Toast.LENGTH_SHORT).show()
            }
        }

        // "Like" düyməsi
        binding.likeButton.setOnClickListener {
            if (isLiked) {
                binding.likeButton.setImageResource(R.drawable.ic_favorite_empty)
                currentSong?.let { sharedViewModel.removeFavorite(it) }
            } else {
                val addedSuccessfully = currentSong?.let { sharedViewModel.addFavorite(it) } ?: false
                if (addedSuccessfully) {
                    binding.likeButton.setImageResource(R.drawable.ic_favorite_full)
                } else {
                    Toast.makeText(requireContext(), "Already in Favorites", Toast.LENGTH_SHORT).show()
                }
            }
            isLiked = !isLiked
        }

        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        sharedViewModel.favoriteSongs.observe(viewLifecycleOwner) {
            currentSong?.let {
                isLiked = sharedViewModel.isFavorite(it)
                binding.likeButton.setImageResource(
                    if (isLiked) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
                )
            }
        }
    }
    private fun playSongAt(index: Int) {
        currentSong = songsList[index]
        updateUI(currentSong!!)
        setupMediaPlayer(currentSong!!)
    }

    private fun updateUI(song: PlaylistItem) {
        binding.songName.text = song.title
        binding.artistName.text = song.artist
        binding.startTime.text = "00:00"
    }

    private fun parseDurationToMillis(duration: String): Long {
        val parts = duration.split(":")
        return (parts[0].toInt() * 60 + parts[1].toInt()) * 1000L
    }

    private fun formatMillisToTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun setupMediaPlayer(song: PlaylistItem) {
        mediaPlayer?.release()
        mediaPlayer?.apply {
            if (!isPlaying) {
                start()
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
                this@MusicFragment.isPlaying = true
            }


        setOnCompletionListener {
                this@MusicFragment.isPlaying = false
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
                Toast.makeText(requireContext(), "Song finished", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}
