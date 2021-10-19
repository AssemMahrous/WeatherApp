package com.example.weatherapp.features.module.data

import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.base.platform.BaseRepository
import com.example.weatherapp.data.local.IWeatherLocalDataSource
import com.example.weatherapp.data.remote.IWeatherRemoteDataSource

class WeatherRepository(
    weatherLocalDataSource: IWeatherLocalDataSource, weatherRemoteDataSource: IWeatherRemoteDataSource
) : BaseRepository<IWeatherLocalDataSource, IWeatherRemoteDataSource>(
    weatherLocalDataSource, weatherRemoteDataSource
), IWeatherRepository {

}