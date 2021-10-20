package com.example.weatherapp.features.module.response

import com.google.gson.annotations.SerializedName

data class Sys(
    @SerializedName("pod") val pod: String
)