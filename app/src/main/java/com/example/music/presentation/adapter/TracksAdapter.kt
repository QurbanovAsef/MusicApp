package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.TrackResponse

class TracksAdapter(
    private val onItemClick: (TrackResponse) -> Unit,
    private val onLikeDislike: (TrackResponse) -> Unit
) : RecyclerView.Adapter<TracksAdapter.TrackViewHolder>() {

    var items: MutableList<TrackResponse> = mutableListOf()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    // Yeni gələn itemləri adapterə əlavə edirik
    fun setItems(newItems: List<TrackResponse>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Favorit trackləri yeniləyən metod
    fun updateFavoriteStatus(favoriteTracks: List<TrackResponse>) {
        items.forEach { track ->
            track.isLiked = favoriteTracks.any { it.slug == track.slug && it.isLiked == true }
        }
        notifyDataSetChanged()
    }

    inner class TrackViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(track: TrackResponse) = with(binding) {
            songTitle.text = track.title ?: "Naməlum Mahnı"
            songArtist.text = track.slug ?: "Naməlum İfaçı"
            songName.text = track.venueName ?: "Naməlum İfaçı"
            songDate.text = track.showDate ?: "Naməlum vaxt"
            Glide.with(root.context)
                .load(track.showAlbumCoverURL)
                .into(songImage)
            root.setOnClickListener { onItemClick(track) }

            favoriteIcon.setImageResource(
                if (track.isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )
            favoriteIcon.setOnClickListener {
                track.isLiked = !(track.isLiked ?: false)
                notifyItemChanged(adapterPosition)
                onLikeDislike(track)
            }
        }
    }
}
