package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.databinding.ItemShowBinding
import com.example.music.data.model.response.Playlist

class PlaylistsAdapter(
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.ShowViewHolder>() {

    var items: List<Playlist> = emptyList()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Playlist>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ShowViewHolder(private val binding: ItemShowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(show: Playlist) = with(binding) {
            showName.text = show.name
            showUsername.text = show.username ?: "username"
            root.setOnClickListener { onItemClick(show) }
        }
    }
}
