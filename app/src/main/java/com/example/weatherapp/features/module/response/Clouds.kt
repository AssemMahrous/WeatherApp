package com.example.weatherapp.features.module.response

import com.google.gson.annotations.SerializedName

data class Clouds(
    @SerializedName("all") val all: Int
)