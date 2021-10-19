package com.example.weatherapp.basemodule.base.data.local

import android.location.Location

interface IDeviceLocationManager {
    fun getCurrentLocation(onSuccess: (location: Location?) -> Unit)
    fun getLastSavedLatitude(): String?
    fun getLastSavedLongitude(): String?
}