package com.example.music.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.databinding.ItemShowBinding
import com.example.music.data.model.response.Show

class ShowAdapter(
    private val onItemClick: (Show) -> Unit
) : RecyclerView.Adapter<ShowAdapter.ShowViewHolder>() {

    var items: List<Show> = emptyList()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Show>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ShowViewHolder(private val binding: ItemShowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(show: Show) = with(binding) {
            showDate.text = show.date
            showVenue.text = show.venueName ?: "Venue name"
            showLocation.text = show.venue?.location ?: "Location not found"
            showTracksCount.text = "Tracks: ${show.venue?.showsCount ?: 0}"

            Glide.with(itemView.context) // Pass the context
                .load(show.albumCoverURL) // URL or local image
                .into(imageShow) // Target ImageView

            root.setOnClickListener { onItemClick(show) }
        }
    }
}
