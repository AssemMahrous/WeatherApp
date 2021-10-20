package com.example.weatherapp.data.local

import com.example.weatherapp.basemodule.base.data.local.SharedPreferencesInterface

class ApplicationLocalDataSource(
    override val sharedPreferences: SharedPreferencesInterface,
) : IApplicationLocalDataSource