package com.example.music.data.entity

import com.example.music.data.model.response.Show
import com.example.music.data.model.response.Song

data class MusicEntity(
    val slug: String,
    val title: String,
    val artist: String,
    val trackUrl: String,
    val imageUrl: String
)

fun List<Song>.toMusicListFromSongs() = this.map { it.toMusicEntity() }

fun Song.toMusicEntity() = MusicEntity(
    slug = this.slug.orEmpty(),
    trackUrl = this.mp3Url ?: this.trackUrl.orEmpty(),
    imageUrl = this.imageUrl.orEmpty(),
    title = this.title.orEmpty(),
    artist = this.artist.orEmpty(),
)

fun Show.toMusicListFromShows(): List<MusicEntity> {
    val list = mutableListOf<MusicEntity>()

    tracks?.forEach { track ->
        list.addAll(track.songs?.map {
            it.copy(mp3Url = track.mp3Url)
        }?.toMusicListFromSongs() ?: emptyList())
    }

    return list
}
