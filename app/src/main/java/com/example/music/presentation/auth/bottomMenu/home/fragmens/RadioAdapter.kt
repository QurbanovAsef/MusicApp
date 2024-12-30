package com.example.music.presentation.auth.bottomMenu.home.fragmens

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidprojecttest1.R
import com.example.music.data.model.response.RadioStationResponse

class RadioAdapter : RecyclerView.Adapter<RadioAdapter.RadioViewHolder>() {

    private val stations = mutableListOf<RadioStationResponse>()

    fun submitList(newStations: List<RadioStationResponse>) {
        stations.clear()
        stations.addAll(newStations)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radio_station, parent, false)
        return RadioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        holder.bind(stations[position])
    }

    override fun getItemCount() = stations.size

    class RadioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.iv_station_image)
        private val title: TextView = itemView.findViewById(R.id.tv_station_name)

        fun bind(station: RadioStationResponse) {
            title.text = station.name
            Glide.with(itemView.context).load(station.imageUrl).into(image)
            itemView.setOnClickListener {
                // Add logic to play the selected station
            }
        }
    }
}
