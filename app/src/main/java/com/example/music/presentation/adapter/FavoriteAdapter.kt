package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemPlaylistBinding
import com.example.music.data.model.response.PlaylistItem

class FavoriteAdapter(
    private val favoriteSongs: MutableList<PlaylistItem>,
    private val onDislike: (PlaylistItem) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(private val binding: ItemPlaylistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: PlaylistItem, onDislike: (PlaylistItem) -> Unit) {
            // Mətnləri doldur
            binding.songTitle.text = song.title
            binding.songArtistName.text = song.artist
            binding.songDuration.text = song.duration
            // Favorit ikonu hər zaman dolu göstərilir
            binding.favoriteIcon.setImageResource(R.drawable.ic_favorite_full)

            // Dislike (bəyənməmək) klik funksionallığı
            binding.favoriteIcon.setOnClickListener {
                onDislike(song) // Mahnını favoritlərdən çıxarmaq
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding =
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteSongs[position], onDislike)
    }

    override fun getItemCount(): Int = favoriteSongs.size

    /**
     * Seçilmiş elementi favoritlərdən çıxarır.
     */
    fun removeItem(song: PlaylistItem) {
        val position = favoriteSongs.indexOf(song)
        if (position != -1) {
            favoriteSongs.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    /**
     * Bütün siyahını yeniləmək üçün istifadə olunur.
     */
    fun updateData(newSongs: List<PlaylistItem>) {
        favoriteSongs.clear()
        favoriteSongs.addAll(newSongs)
        notifyDataSetChanged()
    }
}
