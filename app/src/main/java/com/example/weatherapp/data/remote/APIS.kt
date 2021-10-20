package com.example.weatherapp.data.remote

import com.example.weatherapp.BuildConfig.BASE_URL


object APIS {
    private const val FORECAST="forecast"
    const val URL_WEATHER = "$BASE_URL$FORECAST"
    const val LAT = "lat"
    const val LON = "lon"
    const val CNT = "cnt"
    const val UNITS = "units"
    const val QUERYDATA = "q"
    const val APPID = "appid"
}