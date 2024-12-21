package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.TrackResponse

class TracksAdapter(
    private val onItemClick: (TrackResponse) -> Unit,
    private val onLikeDislike: (TrackResponse) -> Unit
) : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    var items: List<TrackResponse> = emptyList()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<TrackResponse>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(song: TrackResponse) = with(binding) {
            songTitle.text = song.title ?: "Naməlum Mahnı" // Mahnının adı
            songArtist.text = song.venueName ?: "Naməlum İfaçı" // İfaçının adı
            songDuration.text = "Müddət: ${song.duration ?: "Bilinmir"}" // Mahnının müddəti

            Glide.with(root.context) // Pass the context
                .load(song.showAlbumCoverURL) // URL or local image
                .into(songImage) // Target ImageView

            // Mahnıya toxunduqda detallarına keçid
            root.setOnClickListener { onItemClick(song) }

            favoriteIcon.setOnClickListener {
                onLikeDislike(song)
            }
        }
    }
}
