package ru.androidlearning.weathertesttask.model.web.data_sources

import ru.androidlearning.weathertesttask.model.web.data_sources.api.WeatherDTO

interface WeatherDataSource {
    suspend fun getWeatherByCityName(cityName: String, units: String, language: String): WeatherDTO
    suspend fun getWeatherByCoordinates(units: String, language: String, lat: Double, lon: Double): WeatherDTO
}
