package com.example.music.data.model.response

data class PlaylistsResponse (
    val playlists: List<Playlist>? = null,
    val totalPages: Int? = null,
    val currentPage: Int? = null,
    val totalEntries: Int? = null
)

data class Playlist (
    val id: Int? = null,
    val slug: String? = null,
    val name: String? = null,
    val description: String? = null,
    val username: String? = null,
    val duration: Int? = null,
    val tracksCount: Int? = null,
    val likesCount: Int? = null,
    val updatedAt: String? = null,
    val published: Boolean? = null,
    val likedByUser: Boolean? = null
)