package com.example.weatherapp.data.local

import com.example.weatherapp.basemodule.base.data.local.IDataBase
import com.example.weatherapp.basemodule.base.data.local.SharedPreferencesInterface

class ApplicationLocalDataSource(
    override val sharedPreferences: SharedPreferencesInterface,
    override val dataBase: IDataBase
) : IApplicationLocalDataSource