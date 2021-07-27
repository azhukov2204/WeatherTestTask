package ru.androidlearning.weathertesttask.model

sealed class DataLoadState<T> {
    data class Success<T>(val responseData: T) : DataLoadState<T>()
    data class Error<T>(val error: Throwable) : DataLoadState<T>()
    data class Loading<T>(val progress: Int?) : DataLoadState<T>()
}
