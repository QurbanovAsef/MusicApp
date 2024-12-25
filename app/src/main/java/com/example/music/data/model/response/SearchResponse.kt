package com.example.music.data.model.response

import com.google.gson.annotations.SerializedName

// Main response model
data class SearchResponse(
    @SerializedName("exact_show")
    val exactShow: ExactShow?,
    @SerializedName("other_shows")
    val otherShows: List<OtherShow>?,
    val songs: List<Song>?,
    val venues: Venue?,
    val tracks: List<Track>?,
    val tags: Tag?,
    val playlists: List<Playlist>?
)

// Nested models
data class ExactShow(
    val id: Int,
    val date: String,
    @SerializedName("cover_art_urls")
    val coverArtUrls: Map<String, String>?,
    @SerializedName("album_cover_url")
    val albumCoverUrl: String?,
    @SerializedName("album_zip_url")
    val albumZipUrl: String?,
    val duration: Int,
    val incomplete: Boolean,
    @SerializedName("admin_notes")
    val adminNotes: String?,
    @SerializedName("tour_name")
    val tourName: String?,
    @SerializedName("venue_name")
    val venueName: String? = null,
    val venue: Venue?,
    @SerializedName("taper_notes")
    val taperNotes: String?,
    @SerializedName("likes_count")
    val likesCount: Int,
    @SerializedName("updated_at")
    val updatedAt: String?,
    val tags: List<TagItem>?,
    val tracks: List<String>?,
    @SerializedName("liked_by_user")
    val likedByUser: String?,
    @SerializedName("previous_show_date")
    val previousShowDate: String?,
    @SerializedName("next_show_date")
    val nextShowDate: String?
)

data class OtherShow(
    val id: Int,
    val name: String?,
    val date: String?
)

data class Track(
    val id: Int,
    val title: String?,
    val duration: Int?,
    @SerializedName("mp3_url")
    val mp3Url: String?
)

data class TagItem(
    val name: String?,
    val description: String?,
    val color: String?,
    val priority: Int,
    val notes: String?
)


