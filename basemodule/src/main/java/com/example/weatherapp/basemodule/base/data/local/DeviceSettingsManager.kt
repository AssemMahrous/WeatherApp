package com.example.weatherapp.basemodule.base.data.local

import android.content.Intent
import android.provider.Settings.ACTION_SETTINGS
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.weatherapp.basemodule.base.common.IApplicationContext
import com.example.weatherapp.basemodule.utils.getKoinInstance
import com.google.android.gms.common.ConnectionResult.RESOLUTION_REQUIRED
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import timber.log.Timber

class DeviceSettingsManager : IDeviceSettingsManager {

    private val applicationContext by getKoinInstance<IApplicationContext>()

    override fun checkLocationSettingsEnabled(fragment: Fragment?, askToEnable: Boolean, trackingSource: String, onResult: (isEnabled: Boolean) -> Unit) {
        val locationRequest = LocationRequest().apply { priority = PRIORITY_HIGH_ACCURACY }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        LocationServices.getSettingsClient(applicationContext.context)
                .checkLocationSettings(builder.build())
                .addOnCompleteListener { task ->
                    try {
                        task.getResult(ApiException::class.java).locationSettingsStates
                        updateResult(true, onResult)
                    } catch (e: ApiException) {
                        if (askToEnable && e.statusCode == RESOLUTION_REQUIRED && fragment != null) {
                            try {
                                (e as? ResolvableApiException)?.let { resolvable ->
                                    resolvable.startResolutionForResult(
                                            fragment.requireActivity(),
                                            IDeviceSettingsManager.REQUEST_CHECK_SETTINGS
                                    )

                                }
                                updateResult(false, onResult)
                            } catch (e: Exception) {
                                Timber.e(e, "Error while checking location settings")
                                updateResult(false, onResult)
                            }
                        } else {
                            updateResult(false, onResult)
                        }
                    }
                }
    }

    override fun onLocationSettingGranted(isEnabled: Boolean) {

    }

    private fun updateResult(result: Boolean, onResult: (isEnabled: Boolean) -> Unit) {
        onResult(result)
    }

    override fun openSettings(fragment: Fragment, onResult: (() -> Unit)?) {
        if (onResult != null) {
            fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                onResult()
            }.launch(Intent(ACTION_SETTINGS))
        } else {
            fragment.startActivity(Intent(ACTION_SETTINGS))
        }
    }
}