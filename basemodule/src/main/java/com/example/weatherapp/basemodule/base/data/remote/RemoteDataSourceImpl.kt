package com.example.weatherapp.basemodule.base.data.remote

import retrofit2.Retrofit


open class RemoteDataSourceImpl(open val retrofit: Retrofit) : IRemoteDataSource
