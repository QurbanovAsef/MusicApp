package com.example.music.data.model.response

data class CommentResponse(
    val id: Int,            // Şərhin unikal ID-si
    val userId: Int,        // Şərh yazan istifadəçinin ID-si
    val text: String,       // Şərh mətninin özü
    val createdAt: String,  // Şərhin yaradılma tarixi
    val updatedAt: String   // Şərhin sonuncu dəfə yeniləndiyi tarix (əgər varsa)
)
