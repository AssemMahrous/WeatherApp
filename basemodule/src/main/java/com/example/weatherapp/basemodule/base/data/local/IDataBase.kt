package com.example.weatherapp.basemodule.base.data.local

interface IDataBase {
    fun updateFlat(id: String, startDate: Long, endDate: Long)
}