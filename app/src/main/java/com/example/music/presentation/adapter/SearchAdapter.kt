package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.ExactShow

class SearchAdapter(private val onTrackClick: (ExactShow) -> Unit) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var items: List<ExactShow> = listOf()

    fun setItems(newItems: List<ExactShow>) {
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
        fun bind(exactShow: ExactShow) {
            binding.songArtist.text = exactShow.venueName ?: "Naməlum İfaçı"
            binding.songTitle.text = exactShow.tourName ?: "Naməlum Tur"
            binding.trackIdTextView.text = exactShow.id.toString()


            val trackString = exactShow.tracks?.joinToString(", ") ?: "No tracks available"
            binding.trackIdTextView.text = trackString

            val imageUrl = exactShow.albumCoverUrl

            Glide.with(itemView.context)
                .load(imageUrl)
                .into(binding.songImage)

            binding.root.setOnClickListener {
                onTrackClick(exactShow) // Click etdikdə ExactShow-u geri göndəririk
            }
        }


    }
}
