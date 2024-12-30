package com.example.music.data.model.response

data class RadioStationResponse(
    val id: String, // Stansiya ID-si
    val name: String, // Stansiya adı
    val streamUrl: String, // Canlı yayım linki
    val imageUrl: String // Stansiya şəkili (istəyə bağlı)
)
