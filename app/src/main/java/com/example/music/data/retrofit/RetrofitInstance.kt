package com.example.music.data.retrofit

import com.example.music.data.service.MusicApiService
import com.google.android.gms.common.api.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object RetrofitInstance {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://phish.in/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: MusicApiService by lazy {
        retrofit.create(MusicApiService::class.java)
    }
}
