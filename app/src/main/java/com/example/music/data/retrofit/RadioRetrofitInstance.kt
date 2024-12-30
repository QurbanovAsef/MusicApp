package com.example.music.data.retrofit

import com.example.music.data.service.RadioApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RadioRetrofitInstance {

    private const val BASE_URL = "https://api.example.com/"  // Burada API URL-inizi yazın

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(provideOkHttpClient())
        .build()

    val api: RadioApiService = retrofit.create(RadioApiService::class.java)

    private fun provideOkHttpClient() = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(provideHttpLoggingInterceptor())
        .build()

    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
