package com.example.music.presentation.auth.bottomMenu.home

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
import com.example.androidprojecttest1.databinding.FragmentHomeBinding
import com.example.music.presentation.adapter.PlaylistsAdapter
import com.example.music.presentation.adapter.TracksAdapter
import com.example.music.presentation.auth.bottomMenu.favorite.FavoriteTrackViewModel
import com.example.music.presentation.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val favoriteTrackViewModel by activityViewModels<FavoriteTrackViewModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var playlistsAdapter: PlaylistsAdapter
    private lateinit var tracksAdapter: TracksAdapter

    companion object {
        private var isAlbumLoaded = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        observeData()

        if (!isAlbumLoaded) {
            binding.progressAlbums.isGone = false
            sharedViewModel.getPlaylists()
        } else {
            binding.progressAlbums.isGone = true
        }
    }

    private fun setupAdapters() {
        playlistsAdapter = PlaylistsAdapter { playlist ->
            binding.progressTracks.isGone = false
            sharedViewModel.getPlaylistDetailsBySlug(playlist.slug)
        }

        binding.showRecyclerView.apply {
            adapter = playlistsAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            isVerticalScrollBarEnabled = true
        }
        tracksAdapter = TracksAdapter(
            onItemClick = { trackEntry ->
                findNavController().navigate(
                    HomeFragmentDirections.actionNavHomeToMusicFragment(trackEntry)
                )
            },
            onLikeDislike = { track ->
                favoriteTrackViewModel.toggleFavorite(track)
            }
        )

        binding.songRecyclerView.apply {
            adapter = tracksAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            isVerticalScrollBarEnabled = true
        }
    }
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.playlistsFlow.collectLatest { playlists ->
                playlistsAdapter.setItems(playlists ?: emptyList())

                if (!isAlbumLoaded && playlists?.isNotEmpty() == true) {
                    binding.progressAlbums.isGone = true
                    isAlbumLoaded = true
                }
            }
        }

        sharedViewModel.playerTracks.observe(viewLifecycleOwner) { tracks ->
            binding.progressTracks.isGone = tracks?.isNotEmpty() == true
            tracksAdapter.setItems(tracks ?: emptyList())
        }

        sharedViewModel.loadingTracks.observe(viewLifecycleOwner) { isLoading ->
            binding.progressTracks.isGone = !isLoading
        }

        favoriteTrackViewModel.favoriteTracks.observe(viewLifecycleOwner) { favoriteTracks ->
            val favoriteTrackSlugs = favoriteTracks.map { it.slug }
            val updatedTracks = sharedViewModel.playerTracks.value?.map { track ->
                track.copy(isLiked = favoriteTrackSlugs.contains(track.slug))
            }
            updatedTracks?.let { tracksAdapter.setItems(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}