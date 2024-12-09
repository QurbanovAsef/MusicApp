package com.example.music.data.model.response

import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShowsResponse(
    val shows: List<Show>? = null,
    val songs: List<Song>? = null,
    @SerializedName("total_pages")
    val totalPages: Long? = null,

    @SerializedName("current_page")
    val currentPage: Long? = null,

    @SerializedName("total_entries")
    val totalEntries: Long? = null
) : Parcelable


@Parcelize
data class Show(
    val id: Int? = null,
    val date: String? = null,
    val coverArtUrls: CoverArtUrls? = null,

    @SerializedName("album_cover_url")
    val albumCoverURL: String? = null,
    val albumZipURL: String? = null,
    val duration: Int? = null,
    val incomplete: Boolean? = null,
    val tourName: TourName? = null,

    @SerializedName("venue_name")
    val venueName: String? = null,
    val venue: Venue? = null,
    val taperNotes: String? = null,
    val likesCount: Int? = null,
    val updatedAt: String? = null,
    val likedByUser: Boolean? = null,
) : Parcelable

@Parcelize
data class Song(
    val slug: String? = null,
    val title: String? = null,
    val artist: String? = null,
    @SerializedName("tracks_count") val tracksCount: Long? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
) : Parcelable
@Parcelize
data class CoverArtUrls(
    val large: String? = null,
    val medium: String? = null,
    val small: String? = null
) : Parcelable

enum class TourName {
    DIVIDED_SKY_FOUNDATION_BENEFIT_2024,
    SUMMER_TOUR_2024
}


@Parcelize
data class Venue(
    val slug: String? = null,
    val name: String? = null,
    val otherNames: List<String>? = null,
    val latitude: Double? = null,
    val Intitude: Double? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val location: String? = null,

    @SerializedName("shows_count")
    val showsCount: Int? = null,

    val updatedAt: String? = null
) : Parcelable
