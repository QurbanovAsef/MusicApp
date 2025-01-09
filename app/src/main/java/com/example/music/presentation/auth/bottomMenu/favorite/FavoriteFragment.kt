package com.example.music.presentation.auth.bottomMenu.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentFavoriteBinding
import com.example.music.presentation.adapter.FavoriteAdapter

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private val favoriteTrackViewModel: FavoriteTrackViewModel by activityViewModels()
    private lateinit var adapter: FavoriteAdapter
    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FavoriteAdapter(
            onItemClick = { selectedTrack ->
                // MusicFragment-ə keçid
                val bundle = Bundle().apply {
                    putParcelable("track", selectedTrack)
                }
                findNavController().navigate(R.id.action_favoriteFragment_to_musicFragment, bundle)
            },
            onLikeDislike = { track -> favoriteTrackViewModel.toggleFavorite(track) }
        )

        binding.favoriteRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FavoriteFragment.adapter
        }

        favoriteTrackViewModel.favoriteTracks.observe(viewLifecycleOwner) { updatedTracks ->
            adapter.updateData(updatedTracks)
        }

    }
}
