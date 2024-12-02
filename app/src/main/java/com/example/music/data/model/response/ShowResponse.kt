package com.example.music.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShowResponse(
    val id: Int = 0, // Default olaraq 0
    val date: String = "", // Default olaraq boş string
    val venue: String = "",
    val location: String = "",
    val tracks_count: Int? = 0 // Default olaraq 0
) : Parcelable


data class AllShowsResponse(
    val shows: List<ShowResponse>
)