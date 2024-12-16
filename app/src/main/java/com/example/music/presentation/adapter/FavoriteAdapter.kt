package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.Song
class FavoriteAdapter(
    private val onItemClick: (Song) -> Unit,
    private val onLikeDislike: (Song) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var songs: List<Song> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song, onItemClick, onLikeDislike)
    }

    override fun getItemCount(): Int = songs.size

    fun updateData(newSongs: List<Song>) {
        songs = newSongs
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song, onItemClick: (Song) -> Unit, onLikeDislike: (Song) -> Unit) {
            binding.songTitle.text = song.title ?: "Unknown Song"
            binding.songArtist.text = song.artist ?: "Unknown Artist"

            binding.root.setOnClickListener { onItemClick(song) }

            binding.favoriteIcon.setImageResource(
                if (song.isLiked) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )

            binding.favoriteIcon.setOnClickListener { onLikeDislike(song) }
        }
    }
}
