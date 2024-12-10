package com.example.music.presentation.auth.bottomMenu.music

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentMusicBinding
import com.example.music.data.model.response.Show
import com.example.music.data.model.response.Song
import com.example.music.presentation.viewmodel.SharedViewModel

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isLiked = false
    private var currentSong: Song? = null
    private var songsList: List<Song> = listOf()
    private var currentSongIndex = 0
    private val handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Argumentlərdən mahnı və ya şou məlumatını alırıq
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
                updateSeekBar() // Start updating the SeekBar
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

        // Like düyməsi
        binding.likeButton.setOnClickListener {
            if (isLiked) {
                binding.likeButton.setImageResource(R.drawable.ic_favorite_empty)
                currentSong?.let { sharedViewModel.removeFavorite(it) }
            } else {
                val addedSuccessfully = currentSong?.let { sharedViewModel.addFavorite(it) } ?: false
                if (addedSuccessfully as Boolean) {
                    binding.likeButton.setImageResource(R.drawable.ic_favorite_full)
                } else {
                    Toast.makeText(requireContext(), "Already in Favorites", Toast.LENGTH_SHORT).show()
                }
            }
            isLiked = !isLiked
        }

        // Geri gedən düymə
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        // Favoritləri izləmək
        sharedViewModel.favoriteSongs.observe(viewLifecycleOwner) {
            currentSong?.let {
                isLiked = sharedViewModel.isFavorite(it)
                binding.likeButton.setImageResource(
                    if (isLiked) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
                )
            }
        }

        // SeekBar-ın toxunulabilir olması
        binding.progressBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    binding.startTime.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    // Mahnı siyahısında mövcud olan bir mahnını oynamaq
    private fun playSongAt(index: Int) {
        currentSong = songsList[index]
        updateUI(currentSong!!)
        setupMediaPlayer(currentSong!!)
    }

    // UI-nı yeniləyirik
    private fun updateUI(song: Song) {
        binding.songName.text = song.title
        binding.artistName.text = song.artist
        binding.startTime.text = "00:00"
        binding.endTime.text = formatTime(song.tracksCount?.toInt() ?: 0)
    }

    // MediaPlayer qurulması və oynadılması
    private fun setupMediaPlayer(song: Song) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            song.url?.let {
                setDataSource(it) // Song URL-sini stream etmək
            } ?: run {
                Toast.makeText(requireContext(), "Song URL is missing", Toast.LENGTH_SHORT).show()
                return@apply
            }
            prepareAsync()
            setOnPreparedListener {
                start()
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
                this@MusicFragment.isPlaying = true
                updateSeekBar() // Start updating the SeekBar
            }

            setOnCompletionListener {
                this@MusicFragment.isPlaying = false
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
                Toast.makeText(requireContext(), "Song finished", Toast.LENGTH_SHORT).show()
                binding.progressBar.progress = 0 // Reset SeekBar after song completion
                binding.startTime.text = "00:00"
            }

            setOnErrorListener { mp, what, extra ->
                Toast.makeText(requireContext(), "Error: $what, $extra", Toast.LENGTH_SHORT).show()
                false
            }
        }
    }

    // SeekBar-ı yeniləyirik
    private fun updateSeekBar() {
        val totalDuration = mediaPlayer?.duration ?: 0
        binding.progressBar.max = totalDuration

        val updateRunnable = object : Runnable {
            override fun run() {
                val currentPosition = mediaPlayer?.currentPosition ?: 0
                binding.progressBar.progress = currentPosition
                binding.startTime.text = formatTime(currentPosition)
                handler.postDelayed(this, 1000) // Update every second
            }
        }

        handler.postDelayed(updateRunnable, 0)
    }

    // Format time as MM:SS
    private fun formatTime(milliseconds: Int): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / 1000) / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    // Fragmentdən ayrılarkən MediaPlayer resurslarını təmizləyirik
    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        _binding = null
    }
}
