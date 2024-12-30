package com.example.music.data.service

import com.example.music.data.model.response.RadioStationResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface RadioApiService {
    // Radio stations list API endpoint
    @GET("radio-stations")
    suspend fun getRadioStations(): Response<List<RadioStationResponse>>
}
