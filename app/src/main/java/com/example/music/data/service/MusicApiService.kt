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

    // Bütün Şouların Alınması
    @GET("shows")
    suspend fun getAllShows(): Response<ShowsResponse>

    // Şouya aid treklərin alınması
    @GET("shows/{show_date}/tracks")
    suspend fun getTracksByShow(
        @Path("show_date") showDate: String
    ): Response<List<ShowsResponse>>


    // Bütün mahnıların siyahısı
    @GET("songs")
    suspend fun getSongs(): Response<SongsResponse>

    // Mahnının detalları
    @GET("songs/{slug}")
    suspend fun getSongDetails(
        @Path("slug") id: Int
    ): Response<Song>

    @GET("tracks")
    suspend fun getTracks(): Response<List<TrackResponse>>

    @GET("playlists/{slug}")
    suspend fun getPlaylistDetails(@Path("slug") slug: String): Response<PlaylistsDetailsResponse>

    @GET("playlists")
    suspend fun getPlaylists(): Response<PlaylistsResponse>

    @GET("search/{term}")
    suspend fun searchExactShows(@Path("term") term: String): Response<SearchResponse>

}
