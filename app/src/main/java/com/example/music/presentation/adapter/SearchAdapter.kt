package com.example.music.presentation.adapter

import android.annotation.SuppressLint
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

    var items: List<TrackResponse> = listOf()
        private set

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<TrackResponse>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(
        holder: SearchViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty() && payloads.contains("TOGGLE_FAVORITE")) {
            holder.binding.favoriteIcon.setImageResource(
                if (items[position].isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }


    override fun getItemCount(): Int = items.size

    inner class SearchViewHolder(val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(track: TrackResponse) = with(binding) {
            // İkonu düzgün vəziyyətə gətiririk
            favoriteIcon.setImageResource(
                if (track.isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
            )

            songTitle.text = track.title ?: "Naməlum Mahnı"
            songName.text = track.venueName ?: "Naməlum Mahnı"
            songArtist.text = track.slug ?: "Naməlum İfaçı"
            trackIdTextView.text = track.id.toString()

            Glide.with(root.context)
                .load(track.showAlbumCoverURL)
                .placeholder(R.drawable.blackicon)
                .into(songImage)

            favoriteIcon.setOnClickListener {
                track.isLiked = !(track.isLiked == true)

                favoriteIcon.setImageResource(
                    if (track.isLiked == true) R.drawable.ic_favorite_full else R.drawable.ic_favorite_empty
                )

                onLikeDislike(track)
            }

            root.setOnClickListener {
                onItemClick(track)
            }
        }

    }
}

