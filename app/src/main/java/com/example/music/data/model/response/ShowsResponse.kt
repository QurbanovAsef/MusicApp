package com.example.music.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShowsResponse(
    val shows: List<Show>? = null,
    val totalPages: Int? = null,
    val currentPage: Int? = null,
    val totalEntries: Int? = null
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
data class CoverArtUrls(
    val large: String? = null,
    val medium: String? = null,
    val small: String? = null
) : Parcelable

enum class TourName {
    DividedSkyFoundationBenefit2024,
    SummerTour2024
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
