package com.example.weatherapp.basemodule.base.common

import android.content.Context
import androidx.annotation.StringRes
import com.example.weatherapp.basemodule.utils.getKoinInstance
import java.util.*

class ApplicationResource : IApplicationResource {
    private val applicationContext by getKoinInstance<IApplicationContext>()
    private val context: Context
        get() = applicationContext.context

    override fun getString(@StringRes id: Int): String {
        return context.getString(id)
    }

    override fun getString(@StringRes id: Int, vararg formatArgs: Any): String {
        return context.getString(id, *formatArgs)
    }

    override fun getQuantityString(id: Int, quantity: Int, vararg formatArgs: Any): String {
        return context.resources.getQuantityString(id, quantity, *formatArgs)
    }

    override fun getStringEnglishNumbers(id: Int, vararg formatArgs: Any): String {
        return getString(id).format(Locale.ENGLISH, *formatArgs)
    }

    override fun getDimensionPixelSize(id: Int): Int {
        return context.resources.getDimensionPixelSize(id)
    }
}