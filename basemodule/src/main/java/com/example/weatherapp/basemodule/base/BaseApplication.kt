package com.example.weatherapp.basemodule.base

import android.app.Application
import androidx.annotation.CallSuper
import com.example.weatherapp.basemodule.BuildConfig
import com.example.weatherapp.basemodule.base.di.KoinMainModules
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import okhttp3.Authenticator
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import timber.log.Timber
import java.util.*
import com.example.weatherapp.basemodule.utils.isDebug

abstract class BaseApplication : Application() {

    abstract val modulesList: ArrayList<Module>
    abstract val authenticator: Authenticator
    abstract val baseUrl: String
    abstract val sharedPreferencesName: String
    abstract val isDebug: Boolean
    lateinit var koin: KoinApplication private set

    private fun initKoin() {
        val modules = KoinMainModules.initApplicationModule(
            baseUrl,
            sharedPreferencesName,
            isDebug,
            authenticator
        )

        modules.addAll(modulesList)

        koin = startKoin {
            // Koin Android logger
            isDebug { androidLogger(Level.ERROR) }
            //inject Android context
            androidContext(this@BaseApplication)
            modules(modules)
        }
    }

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        if (isDebug) {
            val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true) // (Optional) Whether to show thread info or not. Default true
                .methodCount(2) // (Optional) How many method line to show. Default 2
                .methodOffset(5) // (Optional) Hides internal method calls up to offset. Default 5
                .tag("weatherapp") // (Optional) Global tag for every log. Default PRETTY_LOGGER
                .build()

            Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
                override fun isLoggable(priority: Int, tag: String?): Boolean {
                    return isDebug
                }
            })

            // If debug will add debug tree
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    Logger.log(priority, tag, message, t)
                }
            })

            // If beta will also add release tree
            if (BuildConfig.BUILD_TYPE == "beta") Timber.plant(getTimberReleaseTree())

        } else {
            Timber.plant(getTimberReleaseTree())
        }

        initKoin()
    }

    abstract fun getTimberReleaseTree(): Timber.Tree
}