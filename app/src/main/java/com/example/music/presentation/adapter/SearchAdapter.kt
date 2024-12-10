package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.databinding.ItemSongBinding
import com.example.music.data.model.response.Song

class SearchAdapter(
    private val onItemClick: (Song) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SongViewHolder>() {

    private var items: List<Song> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Song>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song) {
            binding.songTitle.text = song.title
            binding.songArtist.text = song.artist
            binding.root.setOnClickListener { onItemClick(song) }
        }
    }
}
