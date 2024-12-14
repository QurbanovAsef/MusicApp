package com.example.music.data.service

import com.example.music.data.model.response.SearchResponse
import com.example.music.data.model.response.ShowsResponse
import com.example.music.data.model.response.Song
import com.example.music.data.model.response.SongsResponse
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

    // Mahnıların axtarılması
    @GET("search/{key}")
    suspend fun searchSongs(@Path("key") key: String): Response<SearchResponse> // Axtarış üçün yeni metod
}
