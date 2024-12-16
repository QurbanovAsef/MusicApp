package com.example.music.presentation.auth.bottomMenu.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentFavoriteBinding
import com.example.music.presentation.adapter.FavoriteAdapter
import com.example.music.presentation.viewmodel.SharedViewModel

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var adapter: FavoriteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)

        // ViewModel-i inicializasiya edirik
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        // Adapteri inicializasiya edirik
        adapter = FavoriteAdapter(
            onItemClick = { selectedSong ->

            },
            onLikeDislike = { song ->
                // Burada mahnının 'like' və ya 'dislike' vəziyyətini dəyişə bilərsiniz
                sharedViewModel.toggleFavorite(song)
            }
        )

        // RecyclerView üçün LayoutManager və Adapter təyin edilir
        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.favoriteRecyclerView.adapter = adapter

        // Favorit mahnılar siyahısını izləyirik
        sharedViewModel.favoriteSongs.observe(viewLifecycleOwner) { updatedSongs ->
            adapter.updateData(updatedSongs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
