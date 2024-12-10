package com.example.music.presentation.auth.bottomMenu.home.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentNewsBinding
import com.example.music.presentation.adapter.ShowAdapter
import com.example.music.presentation.adapter.SongsAdapter
import com.example.music.presentation.viewmodel.SharedViewModel
class NewsFragment : Fragment(R.layout.fragment_news) {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel by viewModels<SharedViewModel>()
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
            val bundle = Bundle().apply {
                putParcelable("show", show)
            }
            findNavController().navigate(R.id.musicFragment, bundle)
        }
        binding.showRecyclerView.adapter = showAdapter
        binding.showRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        songsAdapter = SongsAdapter(
            onItemClick = { song ->
                val bundle = Bundle().apply {
                    putParcelable("song", song)
                }
                findNavController().navigate(R.id.musicFragment, bundle)
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
