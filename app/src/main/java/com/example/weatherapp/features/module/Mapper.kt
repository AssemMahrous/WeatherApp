package com.example.weatherapp.features.module

import com.example.weatherapp.basemodule.utils.getTimeShortFormat
import com.example.weatherapp.basemodule.utils.roundOffDecimal
import com.example.weatherapp.features.module.domain.Weather
import com.example.weatherapp.features.module.domain.WeatherEntity
import com.example.weatherapp.features.module.response.DayResponse
import com.example.weatherapp.features.module.response.WeatherResponse
import com.example.weatherapp.features.module.view.WeatherView
import com.example.weatherapp.features.module.view.WeatherViewEntity

object Mapper {
    fun WeatherResponse.toWeather() =
        Weather(
            cityName = city.name,
            list = list.toWeatherEntity(),
            sunriseTime = getTimeShortFormat(city.sunrise.toLong()),
            sunsetTime = getTimeShortFormat(city.sunset.toLong())
        )

    fun List<DayResponse>.toWeatherEntity() =
        this.map {
            WeatherEntity(
                temp = roundOffDecimal(it.main.temp).toString() ,
                Description = it.weather[0].description,
                windSpeed = roundOffDecimal(it.wind.speed).toString() + "m/s"
            )
        }

    fun Weather.toWeatherView() =
        WeatherView(
            cityName = cityName,
            list = list.toWeatherViewEntity(sunsetTime,sunriseTime),

        )

    fun List<WeatherEntity>.toWeatherViewEntity(sunsetTime: String, sunriseTime: String) =
        this.map {
            WeatherViewEntity(
                temp = it.temp,
                description = it.Description,
                windSpeed = it.windSpeed,
                dayName = null,
                monthName = null,
                sunsetTime = sunsetTime,
                sunriseTime = sunriseTime
            )
        }
}