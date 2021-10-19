package com.example.weatherapp.basemodule.base.common

import androidx.annotation.DimenRes
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

interface IApplicationResource {
    fun getString(@StringRes id: Int): String
    fun getString(@StringRes id: Int, vararg formatArgs: Any): String
    fun getQuantityString(@PluralsRes id: Int, quantity: Int, vararg formatArgs: Any): String
    fun getStringEnglishNumbers(@StringRes id: Int, vararg formatArgs: Any): String
    fun getDimensionPixelSize(@DimenRes id: Int): Int
}