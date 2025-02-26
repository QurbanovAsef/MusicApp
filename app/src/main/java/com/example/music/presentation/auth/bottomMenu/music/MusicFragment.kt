package com.example.music.presentation.auth.bottomMenu.music

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentMusicBinding
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
    private var originalSongsList: List<TrackResponse> = listOf()
    private var isShuffleEnabled = false
    private var isRepeatEnabled = false
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
            if (songs.isEmpty()) return@observe
            songsList = songs
            currentSongIndex = songsList.indexOfFirst { it.slug == currentSongEntity?.slug }
            if (currentSongIndex == -1) currentSongIndex = 0
            currentSongEntity = songsList[currentSongIndex]
            updateUI(currentSongEntity!!)
            setupMediaPlayer(currentSongEntity!!)
        }

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

        binding.forwardButton.setOnClickListener {
            if (currentSongIndex < songsList.size - 1) {
                currentSongIndex++
                playSongAt(currentSongIndex)
            }
        }

        binding.rewindButton.setOnClickListener {
            if (currentSongIndex > 0) {
                currentSongIndex--
                playSongAt(currentSongIndex)
            }
        }

        binding.likeButton.setOnClickListener {
            currentSongEntity?.let { trackResponse ->
                if (isLiked) {
                    favoriteTrackViewModel.removeFavorite(trackResponse)
                    binding.likeButton.setImageResource(R.drawable.ic_favorite_empty)
                    isLiked = false
                } else {
                    favoriteTrackViewModel.addFavorite(trackResponse)
                    binding.likeButton.setImageResource(R.drawable.ic_favorite_full)
                    isLiked = true
                }
            }
        }

        // Geri düyməsi
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.shuffleButton.setOnClickListener {
            isShuffleEnabled = !isShuffleEnabled
            binding.shuffleButton.setImageResource(
                if (isShuffleEnabled) R.drawable.shuffle_on else R.drawable.shuffle_off
            )

            if (isShuffleEnabled) {
                originalSongsList = songsList // Orijinal siyahını saxla
                songsList = songsList.shuffled()
                currentSongIndex = 0
                playSongAt(currentSongIndex)
            } else {
                songsList = originalSongsList // Əsas siyahını geri qaytar
                currentSongIndex = songsList.indexOfFirst { it.slug == currentSongEntity?.slug }
                if (currentSongIndex == -1) currentSongIndex = 0
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
        updateUI(currentSongEntity!!)
        setupMediaPlayer(currentSongEntity!!)
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

    private fun setupMediaPlayer(song: TrackResponse) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(song.mp3Url ?: return)
            prepareAsync()
            setOnPreparedListener {
                start()
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
                this@MusicFragment.isPlaying = true
                updateSeekBar()
            }
            setOnCompletionListener {
                if (isRepeatEnabled) {
                    playSongAt(currentSongIndex)
                } else if (currentSongIndex < songsList.size - 1) {
                    currentSongIndex++
                    playSongAt(currentSongIndex)
                } else {
                    this@MusicFragment.isPlaying = false
                    binding.playPauseButton.setImageResource(R.drawable.ic_play)
                }
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

    @SuppressLint("DefaultLocale")
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