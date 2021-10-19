package com.example.weatherapp.data.remote

import com.example.weatherapp.basemodule.base.data.remote.IRemoteDataSource
import com.example.weatherapp.data.remote.IWeatherRemoteDataSource

interface IApplicationRemoteDataSource :
    IRemoteDataSource,
    IWeatherRemoteDataSource