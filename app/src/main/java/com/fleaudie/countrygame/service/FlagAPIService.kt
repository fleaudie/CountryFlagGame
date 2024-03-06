package com.fleaudie.countrygame.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FlagAPIService {
    private const val BASE_URL = "https://restcountries.com/v3.1/"

    private val retrofit: Lazy<Retrofit> = lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val countryApiService: FlagAPI by lazy {
        retrofit.value.create(FlagAPI::class.java)
    }
}