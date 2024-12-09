package com.example.music.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidprojecttest1.databinding.ItemShowBinding
import com.example.music.data.model.response.ShowResponse

class ShowAdapter(
    private val onItemClick: (ShowResponse) -> Unit
) : RecyclerView.Adapter<ShowAdapter.ShowViewHolder>() {

    private var items: List<ShowResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowViewHolder {
        val binding = ItemShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShowViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<ShowResponse>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class ShowViewHolder(private val binding: ItemShowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(show: ShowResponse) {
            binding.showDate.text = show.date
            binding.showVenue.text = show.venue
            binding.showLocation.text = show.location
            binding.showTracksCount.text = "Tracks: ${show.tracksCount}"
            binding.root.setOnClickListener { onItemClick(show) }
        }
    }
}
