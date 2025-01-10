package com.example.music.data.service

import com.example.music.data.model.response.PlaylistsDetailsResponse
import com.example.music.data.model.response.PlaylistsResponse
import com.example.music.data.model.response.SearchResponse
import com.example.music.data.model.response.ShowsResponse
import com.example.music.data.model.response.Song
import com.example.music.data.model.response.SongsResponse
import com.example.music.data.model.response.TrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MusicApiService {

    @GET("tracks")
    suspend fun getTracks(): Response<List<TrackResponse>>

    @GET("playlists/{slug}")
    suspend fun getPlaylistDetails(@Path("slug") slug: String): Response<PlaylistsDetailsResponse>

    @GET("playlists")
    suspend fun getPlaylists(): Response<PlaylistsResponse>

    @GET("search/{term}")
    suspend fun searchExactShows(@Path("term") term: String): Response<SearchResponse>

}
