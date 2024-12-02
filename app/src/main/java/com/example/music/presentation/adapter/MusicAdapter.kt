package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemPlaylistBinding
import com.example.music.data.model.response.PlaylistItem

class MusicAdapter(
    private val onItemClick: (PlaylistItem) -> Unit,
    private val onLikeDislike: (PlaylistItem) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var songs: MutableList<PlaylistItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song, onItemClick, onLikeDislike)
    }

    override fun getItemCount(): Int = songs.size

    fun setItems(newSongs: List<PlaylistItem>) {
        songs = newSongs.toMutableList()
        notifyDataSetChanged()
    }

    inner class MusicViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            song: PlaylistItem,
            onItemClick: (PlaylistItem) -> Unit,
            onLikeDislike: (PlaylistItem) -> Unit
        ) {
            binding.songTitle.text = song.title
            binding.songArtistName.text = song.artist
            binding.songDuration.text = song.duration
            binding.favoriteIcon.setImageResource(
                if (song.isLiked) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )

            binding.favoriteIcon.setOnClickListener {
                onLikeDislike(song)
            }

            binding.root.setOnClickListener {
                onItemClick(song)
            }
        }
    }
}
