package com.example.weatherapp.basemodule.base.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.basemodule.utils.getKClass
import com.google.gson.Gson
import java.lang.reflect.Type
import java.util.*
import kotlin.reflect.KClass


class SharedPreferencesUtils constructor(
        private val gson: Gson,
        context: Context,
        sharedPreferencesName: String = "pref"
) : SharedPreferencesInterface {
    private val mPrefs: SharedPreferences =
            context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE)


    override fun putString(key: String, value: String?) {
        mPrefs.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? {
        return mPrefs.getString(key, null)
    }

    override fun putBoolean(key: String, value: Boolean) {
        mPrefs.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String): Boolean {
        return mPrefs.getBoolean(key, false)
    }

    override fun putInt(key: String, value: Int) {
        mPrefs.edit().putInt(key, value).apply()
    }

    override fun putIntAndCommit(key: String, value: Int) {
        mPrefs.edit().putInt(key, value).commit()
    }

    override fun getInt(key: String): Int {
        return mPrefs.getInt(key, 0)
    }

    override fun <T> putObject(key: String, value: T?) {
        mPrefs.edit().putString(key, value?.let { gson.toJson(value) }).apply()
    }

    override fun <T : Any> getObject(key: String, type: KClass<T>): T? {
        val s = mPrefs.getString(key, null) ?: return null
        return gson.fromJson(s, type.java)
    }

    override fun <T : Any> getObject(key: String, type: Class<T>): T? {
        return getObject(key, type.getKClass()!!)
    }

    override fun <T : Any> getObject(key: String, type: Type): T? {
        val s = mPrefs.getString(key, null) ?: return null
        return gson.fromJson(s, type)
    }

    override fun remove(key: String) {
        mPrefs.edit().remove(key).apply()
    }

    override fun clearData() {
        mPrefs.edit().clear().apply()
    }

}

