package com.example.music.presentation.auth.bottomMenu.home.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentNewsBinding
import com.example.music.presentation.adapter.FavoriteAdapter
import com.example.music.presentation.adapter.PlaylistsAdapter
import com.example.music.presentation.adapter.TracksAdapter
import com.example.music.presentation.auth.bottomMenu.music.MusicFragmentDirections
import com.example.music.presentation.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewsFragment : Fragment(R.layout.fragment_news) {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var playlistsAdapter: PlaylistsAdapter
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel.getPlaylists()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Playlists adapter
        playlistsAdapter = PlaylistsAdapter { playlist ->
            sharedViewModel.getPlaylistDetailsBySlug(playlist.slug)

        }
        binding.showRecyclerView.adapter = playlistsAdapter
        binding.showRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        // Tracks adapter
        tracksAdapter = TracksAdapter(
            onItemClick = { trackEntry ->
                sharedViewModel.setPlayerTracks(tracksAdapter.items, trackEntry)
                findNavController().navigate(MusicFragmentDirections.actionMusicFragment(trackEntry))
            },
            onLikeDislike = { track ->
                sharedViewModel.toggleFavorite(track)
            }
        )

        binding.songRecyclerView.adapter = tracksAdapter
        binding.songRecyclerView.layoutManager = LinearLayoutManager(context)

        // Collect playlists and update the adapter
        lifecycleScope.launch {
            sharedViewModel.playlistsFlow.collectLatest { playlists ->
                playlists?.let {
                    playlistsAdapter.setItems(it)
                    binding.progressTracks.isGone = true
                } ?: run {
                    binding.progressTracks.isGone = false
                }
            }
        }

        // Observe player tracks and update the adapter
        sharedViewModel.playerTracks.observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNullOrEmpty()) {
                binding.progressTracks.isGone = false
            } else {
                binding.progressTracks.isGone = true
                tracksAdapter.setItems(tracks)
            }
        }

        sharedViewModel.favoriteTracks.observe(viewLifecycleOwner) { favoriteTracks ->
            val favoriteTrackSlugs = favoriteTracks.map { it.slug }
            val updatedTracks = tracksAdapter.items.map { track ->
                track.isLiked = favoriteTrackSlugs.contains(track.slug)
                track
            }
            tracksAdapter.setItems(updatedTracks)
        }
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

