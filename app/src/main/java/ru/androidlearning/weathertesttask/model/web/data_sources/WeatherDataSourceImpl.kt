package ru.androidlearning.weathertesttask.model.web.data_sources

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.androidlearning.weathertesttask.BuildConfig
import ru.androidlearning.weathertesttask.model.web.data_sources.api.WeatherAPI
import ru.androidlearning.weathertesttask.model.web.data_sources.api.WeatherDTO
import java.io.IOException

class WeatherDataSourceImpl : WeatherDataSource {
    override suspend fun getWeatherByCityName(cityName: String, units: String, language: String): WeatherDTO =
        getRetrofitWeatherAPI().getWeatherByCityName(BuildConfig.WEATHER_API_KEY, cityName, units, language)

    override suspend fun getWeatherByCoordinates(units: String, language: String, lat: Double, lon: Double): WeatherDTO =
        getRetrofitWeatherAPI().getWeatherByCoordinates(BuildConfig.WEATHER_API_KEY, units, language, lat, lon)

    private fun getRetrofitWeatherAPI() =
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(createOkHttpClient(WeatherInterceptor()))
            .build()
            .create(WeatherAPI::class.java)

    private fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(interceptor)
        httpClient.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        return httpClient.build()
    }

    inner class WeatherInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            return chain.proceed(chain.request())
        }
    }

    companion object {
        private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    }
}