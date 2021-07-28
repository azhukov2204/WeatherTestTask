package ru.androidlearning.weathertesttask.model.web

import ru.androidlearning.weathertesttask.model.DataLoadState
import ru.androidlearning.weathertesttask.model.web.data_sources.WeatherDataSource
import ru.androidlearning.weathertesttask.model.web.data_sources.api.WeatherDTO
import java.lang.Exception

class WeatherRepositoryImpl(
    private val weatherDataSource: WeatherDataSource
) : WeatherRepository {
    override suspend fun getWeatherByCityName(cityName: String, units: String, language: String): DataLoadState<WeatherDTO> {
        return try {
            val weatherDTO = weatherDataSource.getWeatherByCityName(cityName, units, language)
            DataLoadState.Success(weatherDTO)
        } catch (e: Exception) {
            e.printStackTrace()
            DataLoadState.Error(e)
        }
    }

    override suspend fun getWeatherByCoordinates(units: String, language: String, lat: Double, lon: Double): DataLoadState<WeatherDTO> {
        return try {
            val weatherDTO = weatherDataSource.getWeatherByCoordinates(units, language, lat, lon)
            DataLoadState.Success(weatherDTO)
        } catch (e: Exception) {
            e.printStackTrace()
            DataLoadState.Error(e)
        }
    }
}
