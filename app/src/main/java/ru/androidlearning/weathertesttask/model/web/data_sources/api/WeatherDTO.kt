package ru.androidlearning.weathertesttask.model.web.data_sources.api

import com.google.gson.annotations.SerializedName

data class WeatherDTO(
    @SerializedName("coord") val coordinates : Coordinates,
    @SerializedName("weather") val weather : List<Weather>,
    @SerializedName("base") val base : String,
    @SerializedName("main") val mainParams : MainParams,
    @SerializedName("visibility") val visibility : Int,
    @SerializedName("wind") val wind : Wind,
    @SerializedName("clouds") val clouds : Clouds,
    @SerializedName("dt") val timeOfDataCalculation : Int,
    @SerializedName("sys") val systemParams : SystemParams,
    @SerializedName("timezone") val timezone : Long,
    @SerializedName("id") val id : Int,
    @SerializedName("name") val locationName : String,
    @SerializedName("cod") val cod : Int
)

data class Coordinates (
    @SerializedName("lon") val lon : Double,
    @SerializedName("lat") val lat : Double
)

data class Weather (
    @SerializedName("id") val id : Int,
    @SerializedName("main") val main : String,
    @SerializedName("description") val description : String,
    @SerializedName("icon") val icon : String
)

data class MainParams (
    @SerializedName("temp") val temp : Double,
    @SerializedName("feels_like") val feels_like : Double,
    @SerializedName("temp_min") val tempMin : Double,
    @SerializedName("temp_max") val tempMax : Double,
    @SerializedName("pressure") val pressure : Int,
    @SerializedName("humidity") val humidity : Int
)

data class Wind (
    @SerializedName("speed") val speed : Double,
    @SerializedName("deg") val deg : Int
)

data class Clouds (
    @SerializedName("all") val all : Int
)

data class SystemParams (
    @SerializedName("type") val type : Int,
    @SerializedName("id") val id : Int,
    @SerializedName("country") val country : String,
    @SerializedName("sunrise") val sunrise : Long,
    @SerializedName("sunset") val sunset : Long
)
