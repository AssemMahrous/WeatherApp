package com.example.weatherapp.features.screens.daily

import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.basemodule.base.data.local.IDeviceLocationManager
import com.example.weatherapp.basemodule.base.platform.BaseViewModel
import com.example.weatherapp.basemodule.utils.ErrorType
import com.example.weatherapp.basemodule.utils.LocalException
import com.example.weatherapp.basemodule.utils.getDate
import com.example.weatherapp.basemodule.utils.getDayNameFromDateFuture
import com.example.weatherapp.basemodule.utils.toLiveData
import com.example.weatherapp.features.module.Mapper.toWeatherView
import com.example.weatherapp.features.module.usecase.GetWeatherDataUseCase
import com.example.weatherapp.features.module.view.WeatherView
import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.utils.Event

class DailyViewModel(
    private val deviceLocationManager: IDeviceLocationManager,
    private val getWeatherDataUseCase: GetWeatherDataUseCase
) : BaseViewModel() {
    private val _weatherView = MutableLiveData<WeatherView>()
    val weatherView = _weatherView.toLiveData()
    fun start(permitted: Boolean, query: String?) {
        if (permitted) {
            deviceLocationManager.getCurrentLocation {
                getWeatherData(it?.latitude, it?.longitude, query)
            }
        } else {
            query?.let {
                getWeatherData(null, null, query)
            } ?: run {
                error.postValue(
                    Event(
                        Result.Error(
                            LocalException(
                                ErrorType.Local.Validation.validationError,
                                errorMessage = "Please provide location or add search Query"
                            )
                        )
                    )
                )
            }
        }
    }

    private fun getWeatherData(lat: Double?, lng: Double?, query: String?) {
        wrapBlockingOperation {
            handleResult(getWeatherDataUseCase(lat, lng, query),
                onSuccess = {
                    val weatherData = it.data.toWeatherView()
                    weatherData.list.forEachIndexed { index, weatherViewEntity ->
                        weatherViewEntity.dayName =
                            if (index == 0) "Today" else getDayNameFromDateFuture(index + 1)
                        weatherViewEntity.monthName = getDate(index)
                    }
                    _weatherView.postValue(weatherData)
                }
            )
        }
    }
}