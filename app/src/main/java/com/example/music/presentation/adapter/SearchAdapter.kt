package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.TrackResponse

class SearchAdapter(private val onTrackClick: (TrackResponse) -> Unit) :
    RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    var items: List<TrackResponse> = listOf()
        private set

    fun setItems(newItems: List<TrackResponse>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val exactShow = items[position]
        holder.bind(exactShow)
    }

    override fun getItemCount(): Int = items.size

    inner class SearchViewHolder(private val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(track: TrackResponse) {
            binding.songArtist.text = track.venueName ?: "Naməlum İfaçı"
            binding.songTitle.text = track.title ?: "Naməlum Tur"
            binding.trackIdTextView.text = track.id.toString()

            val imageUrl = track.showAlbumCoverURL

            Glide.with(itemView.context)
                .load(imageUrl)
                .into(binding.songImage)

            binding.root.setOnClickListener {
                onTrackClick(track)
            }

        }


    }
}
