package com.example.music.data.service

import com.example.music.data.model.response.*
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
    suspend fun getAllSongs(): Response<List<Song>>

    // Mahnının detalları
    @GET("songs/{id}")
    suspend fun getSongDetails(
        @Path("id") id: Int
    ): Response<Song>
}
