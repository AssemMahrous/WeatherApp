package com.example.weatherapp.basemodule.base.data.local

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.basemodule.base.common.IApplicationContext
import com.example.weatherapp.basemodule.base.data.local.IPermissionsManager.Companion.PermissionLauncher
import com.example.weatherapp.basemodule.utils.getKoinInstance

class PermissionsManager : IPermissionsManager {

    private val applicationContext by getKoinInstance<IApplicationContext>()


    override fun checkLocationPermission() = checkPermission(ACCESS_FINE_LOCATION)

    override fun registerForLocationPermission(
            fragment: Fragment,
            onResult: (isPermissionGranted: Boolean, isNeverAskAgainChecked: Boolean) -> Unit
    ): PermissionLauncher {
        return registerForPermission(
                fragment,
                ACCESS_FINE_LOCATION,
                onResult
        )
    }

    private fun registerForPermission(
            fragment: Fragment,
            permission: String,
            onResult: (isPermissionGranted: Boolean, isNeverAskAgainChecked: Boolean) -> Unit
    ): PermissionLauncher {
        return PermissionLauncher(fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            onResult(isGranted, !fragment.shouldShowRequestPermissionRationale(permission))
        }, permission)
    }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(applicationContext.context, permission) == PERMISSION_GRANTED
    }
}