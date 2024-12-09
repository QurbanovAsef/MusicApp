package com.example.music.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistItem(
    val id: Int? = null,
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val albumCoverUrl: String? = null,
    val duration: Int? = null,
    val mp3Url: String? = null,
    val releaseDate: String? = null,
    val likesCount: Int? = null,
    val likedByUser: Boolean? = null,
    val tags: List<String>? = null,
    val isLiked: Boolean = false // Əlavə edilmiş sahə

) : Parcelable
