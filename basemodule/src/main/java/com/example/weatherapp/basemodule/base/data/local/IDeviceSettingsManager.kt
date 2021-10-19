package com.example.weatherapp.basemodule.base.data.local

import androidx.fragment.app.Fragment

interface IDeviceSettingsManager {

    fun checkLocationSettingsEnabled(
            fragment: Fragment? = null,
            askToEnable: Boolean = false,
            trackingSource: String = "",
            onResult: (isEnabled: Boolean) -> Unit
    )

    fun openSettings(fragment: Fragment, onResult: (() -> Unit)? = null)

    fun onLocationSettingGranted(isEnabled: Boolean)

    companion object {
        const val REQUEST_CHECK_SETTINGS = 0x1026
    }
}