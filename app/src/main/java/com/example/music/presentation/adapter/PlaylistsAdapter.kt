package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.androidprojecttest1.databinding.ItemShowBinding
import com.example.music.data.model.response.Playlist

class PlaylistsAdapter(
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistsAdapter.ShowViewHolder>() {

    private var items: List<Playlist> = emptyList()

    // Default şəkil URL-ləri
    private val defaultImageUrls = listOf(
        "https://phish.in/blob/9f7o18592vj6ml13di9t5purjv7v.jpg",
        "https://phish.in/blob/go06iennkv0k7bxw49b0vkb6pewv.jpg",
        "https://phish.in/blob/3vpg8fe2hkf7tkwvqsfrnbd0svud.jpg",
        "https://phish.in/blob/jkicdm12ug3l7nc48flpf7xwwikg.jpg",

        )

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

        fun bind(show: Playlist) = with(binding) {
            showName.text = show.name ?: "Naməlum PlayList"
            showUsername.text = show.username ?: "Naməlum İstifadəçi"

            val imageUrl = if (show.albumCoverURL.isNullOrEmpty()) {
                defaultImageUrls.random() // Random default şəkil URL
            } else {
                show.albumCoverURL
            }

            // Şəkli Glide ilə yükləyin
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(binding.imageShow)

            root.setOnClickListener { onItemClick(show) }
        }
    }
}

