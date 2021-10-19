package com.example.weatherapp.data.remote

import com.example.weatherapp.basemodule.base.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.api.WeatherApi
import retrofit2.Retrofit

class ApplicationRemoteDataSource(override val retrofit: Retrofit) : RemoteDataSourceImpl(retrofit),
    IApplicationRemoteDataSource {
    override val flatsApi: WeatherApi = retrofit.create(WeatherApi::class.java)
}