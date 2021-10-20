package com.example.weatherapp.features.module.domain

data class Weather(
    val list: List<WeatherEntity>,
    val cityName: String,
    val sunriseTime: String,
    val sunsetTime: String,
)

data class WeatherEntity(
    val temp: String,
    val Description: String,
    val windSpeed: String
)
