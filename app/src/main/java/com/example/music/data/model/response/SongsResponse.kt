package com.example.music.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
@Parcelize
data class SongsResponse(
    val songs: List<Song>? = null,
    @SerializedName("total_pages")
    val totalPages: Long? = null,

    @SerializedName("current_page")
    val currentPage: Long? = null,

    @SerializedName("total_entries")
    val totalEntries: Long? = null
) : Parcelable

@Parcelize
data class Song(
    val slug: String? = null,
    val title: String? = null,
    val artist: String? = null,
    val original: Boolean? = null,
    val alias: String? = null,
    @SerializedName("tracks_count")
    val tracksCount: Long? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("url") // Bu sahəni API-dən alacağımız URL üçün əlavə edirik
    val url: String? = null,  // URL sahəsi
    var isLiked: Boolean = false // Yeni sahə
) : Parcelable

