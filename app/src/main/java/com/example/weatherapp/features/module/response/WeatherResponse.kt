package com.example.weatherapp.features.module.response

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("city") val city: City,
    @SerializedName("cnt") val cnt: Int,
    @SerializedName("cod") val cod: String,
    @SerializedName("list") val list: List<DayResponse>,
    @SerializedName("message") val message: Int
)