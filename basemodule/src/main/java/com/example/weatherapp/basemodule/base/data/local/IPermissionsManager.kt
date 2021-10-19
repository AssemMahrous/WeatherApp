package com.example.weatherapp.basemodule.base.data.local

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment

interface IPermissionsManager {

    fun checkLocationPermission(): Boolean

    fun registerForLocationPermission(
            fragment: Fragment,
            onResult: (isPermissionGranted: Boolean,
                       isNeverAskAgainChecked: Boolean) -> Unit
    ): PermissionLauncher

    companion object {
        class PermissionLauncher(
                private val permissionLauncher: ActivityResultLauncher<String>,
                internal val permission: String
        ) {
            fun launch() {
                permissionLauncher.launch(permission)
            }
        }
    }
}