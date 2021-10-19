package com.example.weatherapp.basemodule.base.di

import com.example.weatherapp.basemodule.base.common.ApplicationContext
import com.example.weatherapp.basemodule.base.common.ApplicationResource
import com.example.weatherapp.basemodule.base.common.IApplicationContext
import com.example.weatherapp.basemodule.base.common.IApplicationResource
import com.example.weatherapp.basemodule.base.data.local.*
import com.example.weatherapp.basemodule.utils.ConnectivityUtils
import com.example.weatherapp.basemodule.utils.IConnectivityUtils
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

object AppHelpersModule {
    lateinit var module: Module
        private set

    fun init() {
        this.module = module {
            // resource helper
            single<IApplicationResource> { ApplicationResource() }

            // resource helper
            single<IApplicationContext> { ApplicationContext(androidContext()) }

            // checking connectivity helper
            single<IConnectivityUtils> { ConnectivityUtils(androidContext()) }

            // device location manager
            single<IDeviceLocationManager> { DeviceLocationManager() }

            // device settings manager
            single<IDeviceSettingsManager> { DeviceSettingsManager() }

            // permissions manager
            single<IPermissionsManager> { PermissionsManager() }
        }
    }
}