package com.example.music.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SongResponse(
    val id: Int,
    val title: String,
    val duration: String,
    val artist: String,
    val album: String,
    val url: String,
    var isLiked: Boolean = false
) : Parcelable
