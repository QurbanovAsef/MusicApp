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

    private var tracks = listOf<TrackResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newTracks: List<TrackResponse>) {
        tracks = newTracks
        notifyDataSetChanged()
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun addItem(track: TrackResponse) {
//        val updatedList = tracks.toMutableList()
//        updatedList.add(track)
//        tracks = updatedList
//        notifyItemInserted(tracks.size - 1)
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun removeItem(track: TrackResponse) {
//        val updatedList = tracks.toMutableList()
//        val index = updatedList.indexOfFirst { it.slug == track.slug }
//        if (index != -1) {
//            updatedList.removeAt(index)
//            tracks = updatedList
//            notifyItemRemoved(index)
//        }
//    }

    inner class TrackViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: TrackResponse) = with(binding) {
            songTitle.text = track.title ?: "Naməlum Mahnı"
            songName.text = track.venueName ?: "Naməlum Mahnı"
            songArtist.text = track.slug ?: "Naməlum İfaçı"
            trackIdTextView.text = track.id.toString()

            Glide.with(root.context)
                .load(track.showAlbumCoverURL)
                .placeholder(R.drawable.blackicon)
                .into(songImage)

            favoriteIcon.setImageResource(
                if (track.isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )
            favoriteIcon.setOnClickListener {
                onLikeDislike(track)
                notifyItemChanged(adapterPosition)
            }
            root.setOnClickListener {
                onItemClick(track)
            }
        }
    }
}