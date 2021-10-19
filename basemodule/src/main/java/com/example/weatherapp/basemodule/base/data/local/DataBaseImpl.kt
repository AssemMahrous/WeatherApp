package com.example.weatherapp.basemodule.base.data.local

import android.content.Context
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DataBaseImpl constructor(
    context: Context
) : IDataBase {

    private val database: weatherappDatabase
    private val flatsDao: FlatsDao
    private val ioExecutor: Executor

    init {
        database = weatherappDatabase.getInstance(context)
        flatsDao = database.flatsDao()
        ioExecutor = Executors.newSingleThreadExecutor()
    }

    override fun updateFlat(id: String, startDate: Long, endDate: Long) {
        ioExecutor.execute {
            flatsDao.updateFlat(
                startDate = startDate,
                endDate = endDate,
                id = id
            )
        }
    }
}