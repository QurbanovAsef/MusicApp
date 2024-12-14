package com.example.music.data.model.response

import com.google.gson.annotations.SerializedName


data class SearchResponse(

    @SerializedName("other_shows")
    val shows: List<Show>
)