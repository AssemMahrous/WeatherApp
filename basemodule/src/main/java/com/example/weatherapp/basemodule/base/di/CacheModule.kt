package com.example.weatherapp.basemodule.base.di

import com.example.weatherapp.basemodule.base.data.local.SharedPreferencesInterface
import com.example.weatherapp.basemodule.base.data.local.SharedPreferencesUtils
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object CacheModule {
    private const val QUALIFIER_SHARED_PREFERENCES_NAME = "shared-preferences-name"

    lateinit var module: Module
        private set

    fun init(sharedPreferencesName: String) {
        this.module = module {
            single(named(QUALIFIER_SHARED_PREFERENCES_NAME)) { sharedPreferencesName }
            single<SharedPreferencesInterface> {
                SharedPreferencesUtils(get(), get(), sharedPreferencesName)
            }
        }
    }
}