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
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentMusicBinding
import com.example.music.data.model.response.TrackResponse
import com.example.music.presentation.viewmodel.SharedViewModel

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isLiked = false
    private var currentSongEntity: TrackResponse? = null
    private var songsList: List<TrackResponse> = listOf()
    private var currentSongIndex = 0
    private val handler = Handler()

    private val args : MusicFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentSongEntity = args.track

        currentSongEntity?.let {
            updateUI(it)
            isLiked = sharedViewModel.isFavorite(it)
            updateLikeButton()
        }

        sharedViewModel.playerTracks.observe(viewLifecycleOwner) { songs ->
            if (songs.isEmpty()) {
                Toast.makeText(requireContext(), "No song found", Toast.LENGTH_SHORT).show()
                return@observe
            }

            songsList = songs
            if (currentSongEntity == null) {
                currentSongEntity = songs[0]
                currentSongIndex = 0
            } else {
                currentSongIndex =
                    songsList.indexOfFirst { it.slug == currentSongEntity?.slug }
                if (currentSongIndex == -1) {
                    currentSongEntity = songs[0]
                    currentSongIndex = 0
                }
            }
            currentSongEntity?.let {
                updateUI(it)
                setupMediaPlayer(it)
            }
        }

        mediaPlayer?.setOnCompletionListener {
            if (currentSongIndex < songsList.size - 1) {
                currentSongIndex++
                playSongAt(currentSongIndex)
            } else {
                Toast.makeText(requireContext(), "No next song", Toast.LENGTH_SHORT).show()
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
                binding.SeekBar.progress = 0
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
                currentSongEntity?.let { sharedViewModel.removeFavorite(it) }
            } else {
                val addedSuccessfully =
                    currentSongEntity?.let { sharedViewModel.addFavorite(it) } ?: false
                if (addedSuccessfully as Boolean) {
                    binding.likeButton.setImageResource(R.drawable.ic_favorite_full)
                } else {
                    Toast.makeText(requireContext(), "Already in Favorites", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            isLiked = !isLiked
        }

        // Geri gedən düymə
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        // Favoritləri izləmək

        // FavoriteTracks-u izləmək
        sharedViewModel.favoriteTracks.observe(viewLifecycleOwner) { favoriteSongs ->
            currentSongEntity?.let { song ->
                // FavoriteTrack obyektini ilə TrackResponse obyektini müqayisə etmək üçün
                isLiked = favoriteSongs.any { it.slug == song.slug }
                binding.likeButton.setImageResource(
                    if (isLiked) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
                )
            }
        }


        // SeekBar-ın toxunulabilir olması
        binding.SeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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
        currentSongEntity = songsList[index]
        currentSongEntity?.let {
            updateUI(it)
            setupMediaPlayer(it)
        }
    }


    private fun updateUI(songEntity: TrackResponse) {
        binding.songTitle.text = songEntity.title
        binding.artistName.text = songEntity.slug

        Glide.with(requireContext()) // Pass the context
            .load(songEntity.showAlbumCoverURL) // URL or local image
            .into(binding.songImage) // Target ImageView
    }

    private fun handleLikeDislike(song: TrackResponse) {
        if (isLiked) {
            sharedViewModel.removeFavorite(song)
        } else {
            sharedViewModel.addFavorite(song)
        }
        isLiked = !isLiked
        updateLikeButton()
    }

    private fun updateLikeButton() {
        binding.likeButton.setImageResource(
            if (isLiked) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
        )
    }

    // MediaPlayer qurulması və oynadılması
    private fun setupMediaPlayer(song: TrackResponse) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            song.mp3Url?.let {
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
                binding.SeekBar.progress = 0 // Reset SeekBar after song completion
                binding.startTime.text = "00:00"
            }

            setOnErrorListener { mp, what, extra ->
                Toast.makeText(requireContext(), "Error: $what, $extra", Toast.LENGTH_SHORT).show()
                Log.d("MediaPlayerLog", "Song:   ${song.mp3Url}")
                false
            }
        }
    }

    // SeekBar-ı yeniləyirik
    private fun updateSeekBar() = with(binding) {
        val totalDuration = mediaPlayer?.duration ?: 0
        SeekBar.max = totalDuration
        endTime.text = formatTime(totalDuration)

        val updateRunnable = object : Runnable {
            override fun run() {
                if (isAdded) {
                    val currentPosition = mediaPlayer?.currentPosition ?: 0
                    SeekBar.progress = currentPosition
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



