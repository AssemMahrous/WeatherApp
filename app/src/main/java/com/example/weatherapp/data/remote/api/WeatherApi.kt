package com.example.weatherapp.data.remote.api

import com.example.weatherapp.data.remote.APIS.APPID
import com.example.weatherapp.data.remote.APIS.CNT
import com.example.weatherapp.data.remote.APIS.LAT
import com.example.weatherapp.data.remote.APIS.LON
import com.example.weatherapp.data.remote.APIS.QUERYDATA
import com.example.weatherapp.data.remote.APIS.UNITS
import com.example.weatherapp.data.remote.APIS.URL_WEATHER
import com.example.weatherapp.features.module.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET(URL_WEATHER)
    suspend fun getReportData(
        @Query(LAT) latitude: Double?,
        @Query(LON) longitude: Double?,
        @Query(CNT) count: Int?,
        @Query(UNITS) units: String?,
        @Query(QUERYDATA) query: String?,
        @Query(APPID) appId: String?,
    ): Response<WeatherResponse>
}