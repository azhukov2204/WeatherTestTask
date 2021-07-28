package ru.androidlearning.weathertesttask.model.web

import ru.androidlearning.weathertesttask.model.DataLoadState
import ru.androidlearning.weathertesttask.model.web.data_sources.api.WeatherDTO

interface WeatherRepository {
    suspend fun getWeatherByCityName(cityName: String, units: String, language: String): DataLoadState<WeatherDTO>
    suspend fun getWeatherByCoordinates(units: String, language: String, lat: Double, lon: Double): DataLoadState<WeatherDTO>
}