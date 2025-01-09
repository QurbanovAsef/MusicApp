package com.example.music.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrack(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val trackName: String,
    val artistName: String,
    val isLiked: Boolean
)
