package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.FavoriteTrack


class FavoriteAdapter(
    private val onItemClick: (FavoriteTrack) -> Unit,
    private val onLikeDislike: (FavoriteTrack) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    private var favoriteTracks: List<FavoriteTrack> = emptyList()

    fun updateData(updatedTracks: List<FavoriteTrack>) {
        favoriteTracks = updatedTracks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteTracks[position])
    }

    override fun getItemCount(): Int = favoriteTracks.size

    inner class FavoriteViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n")
        fun bind(favoriteTracks: FavoriteTrack) = with(binding) {
            songTitle.text = favoriteTracks.title ?: "Naməlum Mahnı"
            songArtist.text = favoriteTracks.venueName ?: "Naməlum İfaçı"
            songDate.text = favoriteTracks.showDate ?: "Naməlum vaxt"

            Glide.with(root.context)
                .load(favoriteTracks.showAlbumCoverURL)
                .into(songImage)

            binding.favoriteIcon.setImageResource(
                if (favoriteTracks.isLiked) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )
            root.setOnClickListener { onItemClick(favoriteTracks) }
            binding.favoriteIcon.setOnClickListener {
                favoriteTracks.isLiked = !(favoriteTracks.isLiked)
                onLikeDislike(favoriteTracks)
            }
        }
    }
}

