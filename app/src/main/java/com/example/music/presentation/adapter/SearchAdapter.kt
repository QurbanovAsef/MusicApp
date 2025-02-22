package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.TrackResponse

class SearchAdapter(
    private val onItemClick: (TrackResponse) -> Unit,
    private val onLikeDislike: (TrackResponse) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var tracks = emptyList<TrackResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchViewHolder(
        ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int = tracks.size

    fun setItems(newTracks: List<TrackResponse>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

    inner class SearchViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(track: TrackResponse) = with(binding) {
            songTitle.text = track.title.orUnknown("Naməlum Mahnı")
            songName.text = track.venueName.orUnknown("Naməlum Mahnı")
            songArtist.text = track.slug.orUnknown("Naməlum İfaçı")
            trackIdTextView.text = track.id.toString()

            favoriteIcon.setImageResource(if (track.isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty)

            Glide.with(root.context)
                .load(track.showAlbumCoverURL)
                .placeholder(R.drawable.black_icon)
                .into(songImage)

            favoriteIcon.setOnClickListener { toggleLike(track) }
            root.setOnClickListener { onItemClick(track) }
        }

        private fun toggleLike(track: TrackResponse) {
            track.isLiked = track.isLiked != true
            binding.favoriteIcon.setImageResource(if (track.isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty)
            onLikeDislike(track)
        }
    }
}

private fun String?.orUnknown(default: String) = this ?: default
