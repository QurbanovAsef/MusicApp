package com.example.music.data.service

import SongResponse
import com.example.music.data.model.request.CommentRequest
import com.example.music.data.model.request.SongRequest
import com.example.music.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface MusicApiService {

    // **1. Mahnı Axtarışı və Filtrləmə**

    @GET("search")
    suspend fun searchSongs(@Query("term") searchTerm: String): Response<SearchResponse>

    @GET("songs")
    suspend fun getAllSongs(): Response<List<PlaylistItem>>

    @GET("songs")
    suspend fun filterSongs(
        @Query("genre") genre: String?,
        @Query("year") year: Int?,
        @Query("orderBy") orderBy: String?
    ): Response<List<SongResponse>>

    // **2. Mahnı Detalları**

//    @GET("song/{id}")
//    suspend fun getSongDetails(@Path("id") songId: Int): Response<SongDetailResponse>

    @GET("songs/{id}")
    suspend fun getSongDetails(@Path("id") id: Int): Response<PlaylistItem>
    @GET("song/{id}")
    suspend fun getSongById(@Path("id") id: Int): Response<PlaylistItem>
//
//    // **3. Mahnı Oxuma və Yükləmə**
//
//    @GET("song/{id}/play")
//    suspend fun playSong(@Path("id") songId: Int): Response<PlayResponse>

    @GET("song/{id}/download")
    suspend fun downloadSong(@Path("id") songId: Int): Response<DownloadResponse>

    // **4. Mahnı Əlavə, Yeniləmə və Silmə**

    @POST("song")
    suspend fun addSong(@Body songRequest: SongRequest): Response<SongResponse>

    @PUT("song/{id}")
    suspend fun updateSong(
        @Path("id") songId: Int,
        @Body songRequest: SongRequest
    ): Response<SongResponse>

    @DELETE("song/{id}")
    suspend fun deleteSong(@Path("id") songId: Int): Response<Void>

    // **5. Şərhlər**

    @GET("song/{id}/comments")
    suspend fun getSongComments(@Path("id") songId: Int): Response<List<CommentResponse>>

    @POST("song/{id}/comments")
    suspend fun addSongComment(
        @Path("id") songId: Int,
        @Body commentRequest: CommentRequest
    ): Response<CommentResponse>

    // **6. Müəllif Məlumatları**

    @GET("artist/{id}")
    suspend fun getArtistDetails(@Path("id") artistId: Int): Response<ArtistDetailResponse>

    // **7. Şoular və Treklər**

    @GET("shows")
    suspend fun getAllShows(): Response<AllShowsResponse>

    @GET("shows/{show_date}/tracks")
    suspend fun getTracksByShow(
        @Path("show_date") showDate: String
    ): Response<List<SongResponse>>





}
