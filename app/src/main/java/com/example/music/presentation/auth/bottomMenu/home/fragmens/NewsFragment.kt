package com.example.music.presentation.auth.bottomMenu.home.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentNewsBinding
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

        // Set up ShowAdapter and SongsAdapter correctly
        playlistsAdapter = PlaylistsAdapter { playlist ->
            sharedViewModel.getPlaylistDetailsBySlug(playlist.slug)
        }
        binding.showRecyclerView.adapter = playlistsAdapter
        binding.showRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        tracksAdapter = TracksAdapter(
            onItemClick = { trackEntry ->
                sharedViewModel.setPlayerTracks(tracksAdapter.items, trackEntry)
                findNavController().navigate(MusicFragmentDirections.actionMusicFragment(trackEntry))
            },
            onLikeDislike = { song ->
                sharedViewModel.toggleFavorite(song)
            }
        )
        binding.songRecyclerView.adapter = tracksAdapter
        binding.songRecyclerView.layoutManager = LinearLayoutManager(context)

        lifecycleScope.launch {
            sharedViewModel.playlistsFlow.collectLatest { playlists ->
                playlists?.let {
                    playlistsAdapter.setItems(it)
                }
            }
        }

        sharedViewModel.playerTracks.observe(viewLifecycleOwner) { tracks ->
            tracksAdapter.setItems(tracks)
            binding.progressTracks.isGone = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
