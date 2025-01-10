package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.util.Log
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
        "https://phish.in/blob/47d1yu9dkmnepy8y9ezkphe8kcm4.jpg",
        "https://phish.in/blob/krjyc5lwd23hkjtosraz1qi7jml0.jpg",
        "https://phish.in/blob/whntb8tl1afiu04p9vl5mkuqspj9.jpg",
        "https://phish.in/blob/ozwf5gu9sq9ahls1c4fdw0hy88xw.jpg",
        "https://phish.in/blob/25kdiytw3l4zd84d41195i7m1tdi.jpg",
        "https://phish.in/blob/9td940j4brqjwe3o8t3fuac1kih4.jpg",
        "https://phish.in/blob/43w865118qg48xeav1z59912siri.jpg",
        "https://phish.in/blob/syo6960n1wu4dusnqzqv1ocuz1ec.jpg",
        "https://phish.in/blob/yaxmcaaa2o9d71jukf0qpdper2fb.jpg",
        "https://phish.in/blob/f5b1saxweupbi6hgens5tfi25rpa.jpg"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: List<Playlist>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ShowViewHolder(private val binding: ItemShowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(show: Playlist) = with(binding) {
            showName.text = "Album ${adapterPosition + 1}"

            val imageUrl = if (show.albumCoverURL.isNullOrEmpty()) {
                defaultImageUrls[adapterPosition % defaultImageUrls.size] // Random default şəkil URL
            } else {
                show.albumCoverURL
            }

            // Şəkli Glide ilə yükləyin
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.blackicon)
                .into(binding.imageShow)

            root.setOnClickListener { onItemClick(show) }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}
