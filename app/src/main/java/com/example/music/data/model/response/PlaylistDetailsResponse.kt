package com.example.music.data.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class PlaylistsDetailsResponse(
    val id: Int? = null,
    val slug: String? = null,
    val name: String? = null,
    val description: String? = null,
    val username: String? = null,
    val duration: Int? = null,
    val entries: List<TrackEntry>? = null,
    val tracksCount: Int? = null,
    val likesCount: Int? = null,
    val updatedAt: String? = null,
    val published: Boolean? = null,
    val likedByUser: Boolean? = null,
)

@Parcelize
data class TrackEntry(
    val track: TrackResponse? = null,
    val position: Int? = null,
    val duration: Int? = null,
    val startsAtSecond: Int? = null,
    val endsAtSecond: Int? = null,
) : Parcelable
