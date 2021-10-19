package com.example.weatherapp.basemodule.base.data.local

import java.lang.reflect.Type
import kotlin.reflect.KClass

interface SharedPreferencesInterface {

    fun putString(key: String, value: String?)
    fun getString(key: String): String?
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean
    fun putInt(key: String, value: Int)
    fun putIntAndCommit(key: String, value: Int)
    fun getInt(key: String): Int
    fun <T> putObject(key: String, value: T?)
    fun <T : Any> getObject(key: String, type: KClass<T>): T?
    fun <T : Any> getObject(key: String, type: Class<T>): T?
    fun <T : Any> getObject(key: String, type: Type): T?
    fun remove(key: String)
    fun clearData()
}
