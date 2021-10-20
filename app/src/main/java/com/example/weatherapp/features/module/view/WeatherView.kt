package com.example.weatherapp.features.module.view

data class WeatherView(
    val list: List<WeatherViewEntity>,
    val cityName: String,
)

data class WeatherViewEntity(
    val temp: String,
    val description: String,
    val windSpeed: String,
    var dayName: String?,
    var monthName: String?,
    val sunriseTime: String,
    val sunsetTime: String,
)