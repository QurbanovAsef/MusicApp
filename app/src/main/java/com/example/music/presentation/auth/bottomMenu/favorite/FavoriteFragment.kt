package com.example.music.presentation.auth.bottomMenu.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentFavoriteBinding
import com.example.music.data.model.response.TrackResponse
import com.example.music.presentation.adapter.FavoriteAdapter

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {
    private val favoriteTrackViewModel: FavoriteTrackViewModel by activityViewModels()
    private lateinit var adapter: FavoriteAdapter
    private lateinit var binding: FragmentFavoriteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter yaradılması
        adapter = FavoriteAdapter(
            onItemClick = { selectedTrack ->
                // Track məlumatları sadəcə Toast ilə göstərilir
                Toast.makeText(requireContext(), "Selected track: ${selectedTrack.title}", Toast.LENGTH_SHORT).show()
            },
            onLikeDislike = { track -> favoriteTrackViewModel.toggleFavorite(track) }
        )

        // RecyclerView-ya adapter əlavə edilməsi
        binding.favoriteRecyclerView.adapter = adapter

        favoriteTrackViewModel.favoriteTracks.observe(viewLifecycleOwner) { updatedTracks ->
            adapter.updateData(updatedTracks)
        }

        // Favorite CardView tıklama işlemi
        binding.favoriteCardView.setOnClickListener {
            Toast.makeText(requireContext(), "Favorite Card clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
