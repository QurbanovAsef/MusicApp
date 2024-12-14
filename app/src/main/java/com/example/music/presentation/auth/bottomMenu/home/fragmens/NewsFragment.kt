package com.example.music.presentation.auth.bottomMenu.home.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentNewsBinding
import com.example.music.data.entity.toMusicEntity
import com.example.music.data.entity.toMusicListFromShows
import com.example.music.data.entity.toMusicListFromSongs
import com.example.music.presentation.adapter.ShowAdapter
import com.example.music.presentation.adapter.SongsAdapter
import com.example.music.presentation.viewmodel.SharedViewModel

class NewsFragment : Fragment(R.layout.fragment_news) {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var showAdapter: ShowAdapter
    private lateinit var songsAdapter: SongsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up ShowAdapter and SongsAdapter correctly
        showAdapter = ShowAdapter { show ->
            val musicList = show.toMusicListFromShows()

            if (musicList.isEmpty()) {
                sharedViewModel.setPlayerSongs(songsAdapter.items.toMusicListFromSongs())
            } else
                sharedViewModel.setPlayerSongs(musicList)
            findNavController().navigate(R.id.musicFragment)
        }
        binding.showRecyclerView.adapter = showAdapter
        binding.showRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        songsAdapter = SongsAdapter(
            onItemClick = { song ->
                sharedViewModel.setPlayerSongs(songsAdapter.items.toMusicListFromSongs(), song.toMusicEntity())
                findNavController().navigate(R.id.musicFragment)
            },
            onLikeDislike = { song ->
                sharedViewModel.toggleFavorite(song)
            }
        )
        binding.songRecyclerView.adapter = songsAdapter
        binding.songRecyclerView.layoutManager = LinearLayoutManager(context)

        sharedViewModel.getAllSongs()
        sharedViewModel.getAllShows()

        sharedViewModel.allShows.observe(viewLifecycleOwner) { shows ->
            showAdapter.setItems(shows)
        }

        sharedViewModel.allSongs.observe(viewLifecycleOwner) { songs ->
            songsAdapter.setItems(songs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
