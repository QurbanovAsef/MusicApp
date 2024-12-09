package com.example.music.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistItem(
    val id: Int,
    val title: String,
    val artist: String,
    val duration: String,
    val url: String,
    var isLiked: Boolean = false
) : Parcelable
