package com.example.music.presentation.adapter

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

    private var tracks = emptyList<TrackResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavoriteViewHolder(
        ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun updateData(newTracks: List<TrackResponse>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { onItemClick(tracks[adapterPosition]) }
            binding.favoriteIcon.setOnClickListener { toggleLike(tracks[adapterPosition]) }
        }

        fun bind(track: TrackResponse) = with(binding) {
            songTitle.text = track.title ?: "Naməlum Mahnı"
            songArtist.text = track.slug ?: "Naməlum İfaçı"

            Glide.with(root.context)
                .load(track.showAlbumCoverURL)
                .placeholder(R.drawable.black_icon)
                .error(R.drawable.black_icon)
                .into(songImage)

            updateFavoriteIcon(track.isLiked)
        }

        private fun toggleLike(track: TrackResponse) {
            track.isLiked = !(track.isLiked ?: false)
            notifyItemChanged(adapterPosition)
            onLikeDislike(track)
        }

        private fun updateFavoriteIcon(isLiked: Boolean?) {
            binding.favoriteIcon.setImageResource(
                if (isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )
        }
    }
}
