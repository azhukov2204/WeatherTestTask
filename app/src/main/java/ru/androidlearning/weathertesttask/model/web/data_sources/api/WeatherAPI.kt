package ru.androidlearning.weathertesttask.model.web.data_sources.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    @GET("weather")
    suspend fun getWeatherByCityName(
        @Query("appid") apiKey: String,
        @Query("q") cityName: String,
        @Query("units") units: String,
        @Query("lang") language: String
    ): WeatherDTO

    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("appid") apiKey: String,
        @Query("units") units: String,
        @Query("lang") language: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): WeatherDTO
}