package com.example.weatherapp.data.remote

import com.example.weatherapp.basemodule.base.data.remote.IRemoteDataSource
import com.example.weatherapp.data.remote.api.WeatherApi

interface IWeatherRemoteDataSource : IRemoteDataSource {
    val weatherApi: WeatherApi
}