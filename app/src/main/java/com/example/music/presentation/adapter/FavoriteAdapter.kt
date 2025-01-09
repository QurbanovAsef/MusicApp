package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.TrackResponse

class FavoriteAdapter(
    private val onItemClick: (TrackResponse) -> Unit,
    private val onLikeDislike: (TrackResponse) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var tracks = listOf<TrackResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)
    }

    override fun getItemCount() = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newTracks: List<TrackResponse>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // Elementə kliklər üçün dinləyicilər
            binding.root.setOnClickListener {
                onItemClick(tracks[adapterPosition])
            }

            // Like/Dislike ikonunun klik dinləyicisi
            binding.favoriteIcon.setOnClickListener {
                val track = tracks[adapterPosition]
                track.isLiked = !(track.isLiked ?: false)  // Vəziyyəti dəyişdirir
                notifyItemChanged(adapterPosition)        // Dəyişiklikləri yeniləyir
                onLikeDislike(track)                       // ViewModel-ə bildirir
            }
        }

        fun bind(track: TrackResponse) = with(binding) {
            songTitle.text = track.title ?: "Naməlum Mahnı"
            songArtist.text = track.slug ?: "Naməlum İfaçı"

            Glide.with(root.context)
                .load(track.showAlbumCoverURL)
                .placeholder(R.drawable.blackicon)
                .error(R.drawable.blackicon)
                .into(songImage)

            // İkonun vəziyyəti
            favoriteIcon.setImageResource(
                if (track.isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_full
            )
        }
    }

}
