package ru.androidlearning.weathertesttask.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.androidlearning.weathertesttask.model.DataLoadState
import ru.androidlearning.weathertesttask.model.web.WeatherRepository
import ru.androidlearning.weathertesttask.model.web.data_sources.api.WeatherDTO

class MainViewModel : ViewModel(), KoinComponent {
    private val weatherRepository: WeatherRepository by inject()
    private val weatherLiveData: MutableLiveData<DataLoadState<WeatherDTO>> = MutableLiveData()

    fun getWeatherLiveData() = weatherLiveData

    fun getWeatherByCityNameFromServer(cityName: String, units: String, language: String) {
        weatherLiveData.value = DataLoadState.Loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            val dataLoadState = weatherRepository.getWeatherByCityName(cityName, units, language)
            weatherLiveData.postValue(dataLoadState)
        }
    }

    fun getWeatherByCoordinatesFromServer(units: String, language: String, lat: Double, lon: Double) {
        weatherLiveData.value = DataLoadState.Loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            val dataLoadState = weatherRepository.getWeatherByCoordinates(units, language, lat, lon)
            weatherLiveData.postValue(dataLoadState)
        }
    }
}
