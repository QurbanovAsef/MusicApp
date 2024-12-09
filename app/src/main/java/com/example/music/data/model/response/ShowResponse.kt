package com.example.music.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShowResponse(
    val id: Int,
    val name: String,
    val date: String,
    val venue: String,
    val location: String,
    val tracksCount: Int
) : Parcelable
