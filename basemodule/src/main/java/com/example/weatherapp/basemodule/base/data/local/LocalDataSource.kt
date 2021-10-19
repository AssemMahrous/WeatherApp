package com.example.weatherapp.basemodule.base.data.local

interface LocalDataSource {
    val sharedPreferences: SharedPreferencesInterface
    val dataBase: IDataBase
}