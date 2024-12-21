package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.TrackResponse
class FavoriteAdapter(
    private val onItemClick: (TrackResponse) -> Unit,
    private val onLikeDislike: (TrackResponse) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var songs: List<TrackResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song, onItemClick, onLikeDislike)
    }

    override fun getItemCount(): Int = songs.size

    fun updateData(newSongs: List<TrackResponse>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: TrackResponse, onItemClick: (TrackResponse) -> Unit, onLikeDislike: (TrackResponse) -> Unit) {
            binding.songTitle.text = song.title ?: "Unknown TrackResponse"
            binding.songArtist.text = song.venueName ?: "Unknown Artist"

            binding.root.setOnClickListener { onItemClick(song) }

            binding.favoriteIcon.setImageResource(
                if (song.isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )

            binding.favoriteIcon.setOnClickListener { onLikeDislike(song) }
        }
    }
}
