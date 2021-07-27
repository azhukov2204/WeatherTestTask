package ru.androidlearning.weathertesttask.dependencies

import org.koin.dsl.module
import ru.androidlearning.weathertesttask.model.web.WeatherRepository
import ru.androidlearning.weathertesttask.model.web.WeatherRepositoryImpl
import ru.androidlearning.weathertesttask.model.web.data_sources.WeatherDataSource
import ru.androidlearning.weathertesttask.model.web.data_sources.WeatherDataSourceImpl

val weatherNetworkModelDependency = module {
    single<WeatherDataSource> { WeatherDataSourceImpl() }
    single<WeatherRepository> { WeatherRepositoryImpl(get()) }
}

val appComponent = listOf(
    weatherNetworkModelDependency
)
