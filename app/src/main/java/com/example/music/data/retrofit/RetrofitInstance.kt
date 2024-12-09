package com.example.music.data.retrofit

import com.example.music.data.service.MusicApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://phish.in/api/v2/" // API URL

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // JSON dönüşümü
        .build()

    val api: MusicApiService = retrofit.create(MusicApiService::class.java)
}
