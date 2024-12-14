package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.Show

class SearchAdapter(
    private val onItemClick: (Show) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SongViewHolder>() {

    var items: List<Show> = emptyList()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Show>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Show) {
            binding.songTitle.text = song.venueName
            binding.songArtist.text = song.venue?.city
            binding.root.setOnClickListener { onItemClick(song) }
        }
    }
}
