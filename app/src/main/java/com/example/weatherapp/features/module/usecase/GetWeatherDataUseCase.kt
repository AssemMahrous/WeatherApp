package com.example.weatherapp.features.module.usecase

import com.example.weatherapp.basemodule.base.platform.BaseUseCase
import com.example.weatherapp.features.module.data.IWeatherRepository
import com.example.weatherapp.features.module.domain.Weather
import com.example.weatherapp.basemodule.Result

class GetWeatherDataUseCase(repository: IWeatherRepository) :
    BaseUseCase<IWeatherRepository>(repository) {
    suspend operator fun invoke(lat: Double?, lng: Double?, city: String?): Result<Weather> {
        return repository.getWeather(lat, lng, city)
    }
}