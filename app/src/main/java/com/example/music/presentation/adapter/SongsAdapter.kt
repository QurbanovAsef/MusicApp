package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.Song

class SongsAdapter(
    private val onItemClick: (Song) -> Unit,
    private val onLikeDislike: (Song) -> Unit
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    var items: List<Song> = emptyList()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Song>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(song: Song) = with(binding) {
            songTitle.text = song.title ?: "Naməlum Mahnı" // Mahnının adı
            songArtist.text = song.artist ?: "Naməlum İfaçı" // İfaçının adı
            songDuration.text = "Müddət: ${song.tracksCount ?: "Bilinmir"}" // Mahnının müddəti

            Glide.with(root.context) // Pass the context
                .load(song.imageUrl) // URL or local image
                .into(songImage) // Target ImageView

            // Mahnıya toxunduqda detallarına keçid
            root.setOnClickListener { onItemClick(song) }

            favoriteIcon.setOnClickListener {
                onLikeDislike(song)
            }
        }
    }
}
