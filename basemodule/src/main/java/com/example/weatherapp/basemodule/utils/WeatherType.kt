package com.example.weatherapp.basemodule.utils

import com.example.weatherapp.basemodule.R

enum class WeatherType(val type: String, val resource: Int) {
    CLEAR("Clear", R.drawable.sun),
    CLOUDS("Clouds", R.drawable.cloud),
    CATEGORIES("Rain", R.drawable.rain_water_drop),
    OTHER("other", R.drawable.sun);

    companion object {
        private val map = values().associateBy(WeatherType::type)
        fun fromType(type: String?) = map[type]
    }
}