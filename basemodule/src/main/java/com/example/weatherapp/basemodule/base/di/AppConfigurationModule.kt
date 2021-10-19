package com.example.weatherapp.basemodule.base.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppConfigurationModule {
    const val QUALIFIER_IS_DEBUG = "is-debug"

    var isDebug: Boolean = false
        private set

    lateinit var module: Module
        private set

    fun init(isDebug: Boolean) {
        this.isDebug = isDebug
        this.module = module {
            single(named(QUALIFIER_IS_DEBUG)) { isDebug }
        }
    }
}