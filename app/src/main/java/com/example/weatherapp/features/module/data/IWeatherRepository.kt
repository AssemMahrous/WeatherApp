package com.example.weatherapp.features.module.data

import com.example.weatherapp.basemodule.base.platform.IBaseRepository
import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.features.module.domain.Weather

interface IWeatherRepository : IBaseRepository {
    suspend fun getWeather(lat: Double?, lng: Double?, city: String?): Result<Weather>
}