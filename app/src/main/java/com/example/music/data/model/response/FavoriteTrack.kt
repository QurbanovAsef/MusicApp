package com.example.music.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_track")
data class FavoriteTrack(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  // Room avtomatik `id` versin
    val trackName: String,
    val artistName: String,
    val isLiked: Boolean,
    val showAlbumCoverURL: String? = null
)
