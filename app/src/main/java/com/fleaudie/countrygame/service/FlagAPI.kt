package com.fleaudie.countrygame.service

import com.fleaudie.countrygame.model.Country
import retrofit2.Response
import retrofit2.http.GET

interface FlagAPI {
    @GET("independent?status=true")
    suspend fun getIndependentCountries(): Response<List<Country>>
}