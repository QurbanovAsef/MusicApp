package com.example.music.presentation.adapter

import ShowResponse
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.databinding.ItemShowBinding

class ShowAdapter(
    private val onItemClick: (ShowResponse) -> Unit // Burada ShowResponse modelini qəbul edirik
) : RecyclerView.Adapter<ShowAdapter.ShowViewHolder>() {
    private var items: List<ShowResponse> = emptyList() // ShowResponse tipi

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<ShowResponse>) { // ShowResponse tipi
        items = newItems
        notifyDataSetChanged()
    }

    inner class ShowViewHolder(private val binding: ItemShowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(show: ShowResponse) { // ShowResponse tipi
            binding.showDate.text = show.date
            binding.showVenue.text = show.venue_name
            binding.showLocation.text = show.venue.location // Venue obyekti daxilində location var
            binding.showTracksCount.text = "Tracks: ${show.tracks.size}" // Tracks sayı
            binding.root.setOnClickListener { onItemClick(show) }
        }
    }
}
