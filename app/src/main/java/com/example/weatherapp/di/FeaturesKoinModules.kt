package com.example.weatherapp.di

import com.example.weatherapp.data.remote.ApplicationRemoteDataSource
import com.example.weatherapp.data.remote.IApplicationRemoteDataSource
import com.example.weatherapp.data.local.ApplicationLocalDataSource
import com.example.weatherapp.data.local.IApplicationLocalDataSource
import com.example.weatherapp.data.local.IWeatherLocalDataSource
import com.example.weatherapp.data.remote.IWeatherRemoteDataSource
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.experimental.builder.factory
import java.util.*

object FeaturesKoinModules {

    private lateinit var appNetworkModule: Module
    private lateinit var appCacheModule: Module

    val allModules: ArrayList<Module>
        get() {
            val list = arrayListOf<Module>()

            // general
            list.add(appCacheModule)
            list.add(appNetworkModule)


            return list
        }

    fun init() {
        // remote data source
        initAppNetworkModule()

        // local data source
        initAppCacheModule()

    }

    private fun initAppCacheModule() {
        appCacheModule = module {
            single<IApplicationLocalDataSource> {
                ApplicationLocalDataSource(
                    get(),
                    get()
                )
            }
            single<IWeatherLocalDataSource> { get<IApplicationLocalDataSource>() }
        }
    }

    private fun initAppNetworkModule() {
        appNetworkModule = module {
            single<IApplicationRemoteDataSource> { ApplicationRemoteDataSource(get()) }
            single<IWeatherRemoteDataSource> { get<IApplicationRemoteDataSource>() }
        }
    }
}
