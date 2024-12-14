package com.example.music.presentation.auth.bottomMenu.music

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentMusicBinding
import com.example.music.data.entity.MusicEntity
import com.example.music.data.model.response.Show
import com.example.music.presentation.viewmodel.SharedViewModel

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isLiked = false
    private var currentSong: MusicEntity? = null
    private var songsList: List<MusicEntity> = listOf()
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

        // Argumentlərdən mahnı və ya şou məlumatını alırıq
        val show = arguments?.getParcelable<Show>("show")
//        currentSong = arguments?.getParcelable("song")

        // API-dən mahnılar alınır
//        sharedViewModel.getAllSongs()


        currentSong = sharedViewModel.playerActiveMusic
        sharedViewModel.playerMusicList.observe(viewLifecycleOwner) { songs ->
            if (songs.isEmpty()) {
                Toast.makeText(requireContext(), "No song found", Toast.LENGTH_SHORT).show()
                return@observe
            }

            songsList = songs
            if (currentSong == null) {
                currentSong = songs[0]
                currentSongIndex = 0
            } else {
                currentSongIndex = songsList.indexOfFirst { it.slug == currentSong?.slug }
                if (currentSongIndex == -1) {
                    currentSong = songs[0]
                    currentSongIndex = 0
                }
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
            } else {
                mediaPlayer?.start()
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
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
    private fun updateUI(song: MusicEntity) = with(binding) {
        songName.text = song.title
        artistName.text = song.artist
        startTime.text = "00:00"

        Glide.with(requireContext()) // Pass the context
            .load(song.imageUrl) // URL or local image
            .into(songImage) // Target ImageView
    }

    // MediaPlayer qurulması və oynadılması
    private fun setupMediaPlayer(song: MusicEntity) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            song.trackUrl?.let {
                Log.d("DDDDD", it)
                if (it.isEmpty())
                    return
                setDataSource(it) // Song URL-sini stream etmək
            } ?: run {
                Toast.makeText(requireContext(), "Song URL not found", Toast.LENGTH_SHORT).show()
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
                binding.progressBar.progress = 0 // Reset SeekBar after song completion
                binding.startTime.text = "00:00"
            }

            setOnErrorListener { mp, what, extra ->
                Toast.makeText(requireContext(), "Error: $what, $extra", Toast.LENGTH_SHORT).show()
                Log.d("MediaPlayerLog", "Song:   ${song.trackUrl}")
                false
            }
        }
    }

    // SeekBar-ı yeniləyirik
    private fun updateSeekBar() = with(binding) {
        val totalDuration = mediaPlayer?.duration ?: 0
        progressBar.max = totalDuration
        endTime.text = formatTime(totalDuration)

        val updateRunnable = object : Runnable {
            override fun run() {
                if (isAdded) {
                    val currentPosition = mediaPlayer?.currentPosition ?: 0
                    progressBar.progress = currentPosition
                    startTime.text = formatTime(currentPosition)
                    handler.postDelayed(this, 1000) // Update every second
                }
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
