package com.example.weatherapp.basemodule.base.di

import okhttp3.Authenticator
import org.koin.core.module.Module
import java.util.*

object KoinMainModules {
    val moduleList = arrayListOf<Module>()

    fun initApplicationModule(
        baseUrl: String,
        sharedPreferencesName: String,
        isDebug: Boolean,
        authenticator: Authenticator
    ): ArrayList<Module> {
        // application configuration module
        AppConfigurationModule.init(isDebug)
        moduleList.add(AppConfigurationModule.module)

        // serialization module
        SerializationModule.init()
        moduleList.add(SerializationModule.module)

        // local data source module
        CacheModule.init(sharedPreferencesName)
        moduleList.add(CacheModule.module)

        // remote data source module
        NetworkModule.init(baseUrl, authenticator)
        moduleList.add(NetworkModule.module)

        // application helpers module
        AppHelpersModule.init()
        moduleList.add(AppHelpersModule.module)

        return moduleList
    }
}