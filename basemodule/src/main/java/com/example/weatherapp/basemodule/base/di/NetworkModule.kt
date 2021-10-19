package com.example.weatherapp.basemodule.base.di

import com.example.weatherapp.basemodule.base.di.AppConfigurationModule.QUALIFIER_IS_DEBUG
import com.example.weatherapp.basemodule.utils.CustomHttpLoggingInterceptor
import com.google.gson.Gson
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object NetworkModule {
    private const val QUALIFIER_BASE_URL = "base-url"

    lateinit var module: Module
        private set

    fun init(
        baseUrl: String,
        authenticator: Authenticator
    ) {

        this.module = module {
            single(named(QUALIFIER_BASE_URL)) { baseUrl }
            single { authenticator }
            single { provideOkHttp(get(named(QUALIFIER_IS_DEBUG)), get()) }
            single { provideRetrofit(get(named(QUALIFIER_BASE_URL)), get(), get()) }
        }
    }

    private fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okHttpClient)
        .build()

    private fun provideOkHttp(
        isDebug: Boolean,
        authenticator: Authenticator
    ): OkHttpClient {
        val logging = CustomHttpLoggingInterceptor(
            object : CustomHttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.tag("OkHttp").d(message)
                }
            })

        logging.level = CustomHttpLoggingInterceptor.Level.BODY

        val okHttpClientBuilder = OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .addNetworkInterceptor(logging)
            .authenticator(authenticator)
        return okHttpClientBuilder.build()
    }
}