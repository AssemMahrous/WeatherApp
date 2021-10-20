package com.example.weatherapp.features.module.data

import com.example.weatherapp.BuildConfig.APP_ID
import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.base.platform.BaseRepository
import com.example.weatherapp.data.local.IWeatherLocalDataSource
import com.example.weatherapp.data.remote.IWeatherRemoteDataSource
import com.example.weatherapp.features.module.Mapper.toWeather
import com.example.weatherapp.features.module.domain.Weather
import com.example.weatherapp.features.module.response.WeatherResponse

class WeatherRepository(
    weatherLocalDataSource: IWeatherLocalDataSource,
    weatherRemoteDataSource: IWeatherRemoteDataSource
) : BaseRepository<IWeatherLocalDataSource, IWeatherRemoteDataSource>(
    weatherLocalDataSource, weatherRemoteDataSource
), IWeatherRepository {
    override suspend fun getWeather(lat: Double?, lng: Double?, city: String?): Result<Weather> {
        return safeApiCallWithHeaders(networkCall = {
            remoteDataSource.weatherApi.getReportData(
                latitude = lat,
                longitude = lng,
                count = 7,
                units = "metric",
                query = city,
                appId = APP_ID
            )
        }, successHandler = { data: WeatherResponse, _: HashMap<String, String> ->
            data.toWeather()
        })
    }

}