package com.example.weatherapp.basemodule.base.data.local

import android.location.Location
import android.os.Handler
import android.os.Looper
import com.example.weatherapp.basemodule.base.common.IApplicationContext
import com.example.weatherapp.basemodule.utils.getKoinInstance
import com.example.weatherapp.basemodule.utils.isNullOrFalse
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY

class DeviceLocationManager : IDeviceLocationManager {

    private val KEY_LAST_SAVED_LATITUDE = "LAST_SAVED_LATITUDE"
    private val KEY_LAST_SAVED_LONGITUDE = "LAST_SAVED_LONGITUDE"
    private val sharedPreferences by getKoinInstance<SharedPreferencesInterface>()
    private val permissionsManager by getKoinInstance<IPermissionsManager>()
    private val applicationContext by getKoinInstance<IApplicationContext>()
    private val locationRequestTimeout = 3 * 1000L
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var expirationHandler: Handler? = null
    private var expirationRunnable: Runnable? = null

    override fun getLastSavedLatitude(): String? {
        if (permissionsManager.checkLocationPermission()) {
            return sharedPreferences.getString(KEY_LAST_SAVED_LATITUDE)
        }
        return null
    }

    override fun getLastSavedLongitude(): String? {
        if (permissionsManager.checkLocationPermission()) {
            return sharedPreferences.getString(KEY_LAST_SAVED_LONGITUDE)
        }
        return null
    }

    override fun getCurrentLocation(onSuccess: (location: Location?) -> Unit) {
        if (permissionsManager.checkLocationPermission()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext.context)
            fusedLocationClient?.lastLocation?.addOnSuccessListener { location ->
                if (location != null) {
                    updateUserProperties(location)
                    onSuccess(location)
                } else {
                    val locationRequest = LocationRequest().apply {
                        priority = PRIORITY_HIGH_ACCURACY
                        interval = 0
                    }
                    locationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult?) {
                            removeCallbacks()
                            updateUserProperties(location)
                            onSuccess(locationResult?.lastLocation)
                        }

                        override fun onLocationAvailability(availability: LocationAvailability?) {
                            if (availability?.isLocationAvailable.isNullOrFalse()) {
                                removeCallbacks()
                                onSuccess(null)
                            }
                        }
                    }
                    fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    Looper.getMainLooper()?.let { looper ->
                        expirationHandler = Handler(looper)
                        expirationRunnable = Runnable {
                            removeCallbacks()
                            onSuccess(null)
                        }
                        expirationRunnable?.let { expirationHandler?.postDelayed(it, locationRequestTimeout) }
                    }
                }
            }
        }
    }

    private fun updateUserProperties(location: Location?) {
        location?.let {
            sharedPreferences.putString(KEY_LAST_SAVED_LATITUDE, location.latitude.toString())
            sharedPreferences.putString(KEY_LAST_SAVED_LONGITUDE, location.longitude.toString())
        }
    }

    private fun removeCallbacks() {
        expirationRunnable?.let { expirationHandler?.removeCallbacks(it) }
        locationCallback?.let { fusedLocationClient?.removeLocationUpdates(it) }
    }
}