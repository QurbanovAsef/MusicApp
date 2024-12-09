package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.PlaylistItem

class MusicAdapter(
    private val onItemClick: (PlaylistItem) -> Unit,
    private val onLikeDislike: (PlaylistItem) -> Unit
) : RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    private var songs: MutableList<PlaylistItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val song = songs[position]
        holder.bind(song, onItemClick, onLikeDislike)
    }

    override fun getItemCount(): Int = songs.size

    // Yeni verilənləri əlavə etmək üçün
    fun setItems(newSongs: List<PlaylistItem>) {
        songs = newSongs.toMutableList()
        notifyDataSetChanged()
    }

    inner class MusicViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(
            playlistItem: PlaylistItem,
            onItemClick: (PlaylistItem) -> Unit,
            onLikeDislike: (PlaylistItem) -> Unit
        ) {
            binding.songTitle.text = playlistItem.title
            binding.songArtist.text = playlistItem.artist
            binding.songDuration.text = "Müddət: ${playlistItem.duration}"


            binding.root.setOnClickListener { onItemClick(playlistItem) }

            // Favorit ikonu dəyişdirmək
            binding.favoriteIcon.setImageResource(
                if (playlistItem.isLiked) R.drawable.ic_favorite_full
                else R.drawable.ic_favorite_empty
            )

            binding.favoriteIcon.setOnClickListener {
                onLikeDislike(playlistItem)
            }
        }
    }
}
