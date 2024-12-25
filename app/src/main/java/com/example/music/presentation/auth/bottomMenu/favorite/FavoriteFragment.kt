
package com.example.music.presentation.auth.bottomMenu.favorite

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import android.media.MediaPlayer
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.FragmentFavoriteBinding
import com.example.music.data.model.response.FavoriteTrack
import com.example.music.presentation.adapter.FavoriteAdapter
import com.example.music.presentation.viewmodel.SharedViewModel

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var adapter: FavoriteAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoriteBinding.bind(view)


        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        adapter = FavoriteAdapter(
            onItemClick = { selectedTrack ->

                playTrack(selectedTrack)
            },
            onLikeDislike = { favoriteTrack ->

                sharedViewModel.favoriteTracks.observe(viewLifecycleOwner) { updatedTracks ->
                    adapter.updateData(updatedTracks)
                }
            }
        )

        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.favoriteRecyclerView.adapter = adapter

        sharedViewModel.favoriteTracks.observe(viewLifecycleOwner) { updatedTracks ->
            adapter.updateData(updatedTracks)
        }
        sharedViewModel.loadFavoriteTracks()
    }

    private fun playTrack(track: FavoriteTrack) {
        mediaPlayer?.stop()
        mediaPlayer?.release()


        mediaPlayer = MediaPlayer().apply {
            try {

                setDataSource(track.mp3Url ?: "")
                prepare()  // MediaPlayer-i hazırlayırıq
                start()    // Musiqini çalırıq
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
    }

}
