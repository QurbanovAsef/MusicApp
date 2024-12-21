package com.example.music.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class TrackResponse(
    val id: Int? = null,
    val slug: String? = null,
    val title: String? = null,
    val position: Int? = null,
    val duration: Int? = null,
    val setName: String? = null,
    val likesCount: Int? = null,
    val waveformImageURL: String? = null,
    val tags: List<Tag>? = null,
    val updatedAt: String? = null,
    val showDate: String? = null,
    val showCoverArtUrls: ShowCoverArtUrls? = null,

    @SerializedName("show_album_cover_url")
    val showAlbumCoverURL: String? = null,
    val venueSlug: String? = null,
    val venueName: String? = null,
    val venueLocation: String? = null,
    val songs: List<Song>? = null,
    val likedByUser: Boolean? = null,

    @SerializedName("mp3_url")
    val mp3Url: String? = null,

    var isLiked: Boolean? = null
) : Parcelable

@Parcelize
data class ShowCoverArtUrls(
    val large: String? = null,
    val medium: String? = null,
    val small: String? = null
) : Parcelable

@Parcelize
data class Tag(
    val name: String? = null,
    val description: String? = null,
    val color: String? = null,
    val priority: Int? = null,
) : Parcelable
