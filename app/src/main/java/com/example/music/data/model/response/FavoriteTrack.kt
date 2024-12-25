package com.example.music.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "favorite_tracks")
data class FavoriteTrack(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val title: String? = null,
    @SerializedName("venue_name")
    val venueName: String? = null,
    val duration: Int? = null,
    @SerializedName("show_date")
    val showDate: String? = null,
    @SerializedName("show_album_cover_url")
    val showAlbumCoverURL: String? = null,
    val slug: String? = null,
    @SerializedName("mp3_url")
    val mp3Url: String? = null,
    var isLiked: Boolean = false
)

fun TrackResponse.toFavoriteTrack(): FavoriteTrack {
    return FavoriteTrack(
        title = this.title ?: "",
        venueName = this.venueName ?: "",
        mp3Url = this.mp3Url,
        isLiked = this.isLiked ?: false
    )
}