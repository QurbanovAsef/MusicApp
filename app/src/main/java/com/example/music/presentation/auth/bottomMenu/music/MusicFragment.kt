package com.example.music.presentation.auth.bottomMenu.music

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.music.data.model.response.FavoriteTrack
import com.example.music.data.model.response.TrackResponse
import com.example.music.presentation.auth.bottomMenu.favorite.FavoriteTrackViewModel
import com.example.music.presentation.viewmodel.SharedViewModel

class MusicFragment : Fragment() {

    private var _binding: FragmentMusicBinding? = null
    private val binding get() = _binding!!
    private val favoriteTrackViewModel: FavoriteTrackViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isLiked = false
    private var currentSongEntity: TrackResponse? = null
    private var songsList: List<TrackResponse> = listOf()
    private var currentSongIndex = 0
    private val handler = Handler(Looper.getMainLooper())

    private val args: MusicFragmentArgs by navArgs()

    private var isShuffleEnabled = false // Shuffle rejimi
    private var isRepeatEnabled = false // Repeat rejimi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track: TrackResponse? = arguments?.getParcelable("track")
        track?.let {
            binding.songTitle.text = it.title
        }
        currentSongEntity = args.track

        currentSongEntity?.let {
            updateUI(it)
            isLiked = favoriteTrackViewModel.isFavorite(it)
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
            if (isRepeatEnabled) {
                playSongAt(currentSongIndex) // Repeat mahnı
            } else if (currentSongIndex < songsList.size - 1) {
                currentSongIndex++
                playSongAt(currentSongIndex)
            } else {
                Toast.makeText(requireContext(), "No next song", Toast.LENGTH_SHORT).show()
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
                binding.SeekBar.progress = 0
            }
        }

        // Play/Pause düyməsi
        binding.playPauseButton.setOnClickListener {
            if (isPlaying) {
                mediaPlayer?.pause()
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer?.start()
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
                updateSeekBar()
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
            currentSongEntity?.let { trackResponse ->
                val favoriteTrack = FavoriteTrack(
                    id = 0,
                    trackName = trackResponse.title ?: "Naməlum Mahnı",
                    artistName = trackResponse.slug ?: "Naməlum İfaçı",
                    isLiked = true,
                    showAlbumCoverURL = trackResponse.showAlbumCoverURL
                )

                if (isLiked) {
                    binding.likeButton.setImageResource(R.drawable.ic_favorite_empty)
                    if (favoriteTrackViewModel.removeFavorite(trackResponse)) {
                        Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to remove", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (favoriteTrackViewModel.addFavorite(trackResponse)) {
                        binding.likeButton.setImageResource(R.drawable.ic_favorite_full)
                        Toast.makeText(requireContext(), "Added to Favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to add", Toast.LENGTH_SHORT).show()
                    }
                }
                isLiked = !isLiked
            }
        }



        // Geri düyməsi
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        // Shuffle düyməsi
        binding.shuffleButton.setOnClickListener {
            isShuffleEnabled = !isShuffleEnabled
            binding.shuffleButton.setImageResource(
                if (isShuffleEnabled) {
                    R.drawable.shuffle_on
                } else R.drawable.shuffle_off
            )
            if (isShuffleEnabled) {
                shuffleSongs()
            } else {
                playSongAt(currentSongIndex)
            }
        }
        // Repeat düyməsi
        binding.repeatButton.setOnClickListener {
            isRepeatEnabled = !isRepeatEnabled
            binding.repeatButton.setImageResource(
                if (isRepeatEnabled) R.drawable.repeat_on else R.drawable.repeat_off
            )
        }

        // SeekBar
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

    private fun playSongAt(index: Int) {
        currentSongEntity = songsList[index]
        currentSongEntity?.let {
            updateUI(it)
            setupMediaPlayer(it)
        }
    }
    private fun updateUI(songEntity: TrackResponse) {
        binding.songTitle.text = songEntity.title
        binding.songTitle.isSelected = true // Enable marquee for song title
        binding.artistName.text = songEntity.slug
        binding.songName.text = songEntity.venueName
        binding.songName.isSelected = true // Enable marquee for song name

        Glide.with(requireContext())
            .load(songEntity.showAlbumCoverURL)
            .into(binding.songImage)
    }


    private fun updateLikeButton() {
        binding.likeButton.setImageResource(
            if (isLiked) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
        )
    }

    private fun shuffleSongs() {
        songsList = songsList.shuffled()
        currentSongIndex = 0
        playSongAt(currentSongIndex)
    }

    private fun setupMediaPlayer(song: TrackResponse) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            song.mp3Url?.let {
                if (it.isEmpty()) return
                setDataSource(it)
            } ?: run {
                Toast.makeText(requireContext(), "Song URL not found", Toast.LENGTH_SHORT).show()
                return@apply
            }
            prepareAsync()
            setOnPreparedListener {
                start()
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
                this@MusicFragment.isPlaying = true
                updateSeekBar()
            }
        }
    }

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
                    handler.postDelayed(this, 1000)
                }
            }
        }

        handler.postDelayed(updateRunnable, 0)
    }

    private fun formatTime(timeInMillis: Int): String {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}