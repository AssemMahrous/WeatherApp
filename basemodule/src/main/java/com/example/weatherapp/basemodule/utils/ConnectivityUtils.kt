package com.example.weatherapp.basemodule.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.weatherapp.basemodule.R
import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.utils.MessageUtils.showNoInternetErrorWithRetryDialog


class ConnectivityUtils constructor(private val context: Context) : IConnectivityUtils {
    @Suppress("DEPRECATION")
    override val isNetworkConnected: Boolean
        get() {
            var result = false
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cm?.run {
                    cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                        result = when {
                            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                            else -> false
                        }
                    }
                }
            } else {
                cm?.run {
                    cm.activeNetworkInfo?.run {
                        if (type == ConnectivityManager.TYPE_WIFI) {
                            result = true
                        } else if (type == ConnectivityManager.TYPE_MOBILE) {
                            result = true
                        }
                    }
                }
            }
            return result
        }

    companion object {
        val connectivityUtils by getKoinInstance<IConnectivityUtils>()
        val noInternetError = Result.Error(
                NetworkException(
                        type = ErrorType.Network.NoInternetConnection,
                        errorMessageRes = R.string.message_error_no_internet
                )
        )

        @JvmStatic
        inline fun checkForConnectivity(isNotConnected: (() -> Unit), isConnected: (() -> Unit)) {
            when {
                connectivityUtils.isNetworkConnected -> isConnected()
                else -> isNotConnected()
            }
        }

        @JvmStatic
        fun checkForConnectivityAndShowDialog(activity: Activity,
                                              isConnected: () -> Unit,
                                              buttonActionRetry: (() -> Unit)?,
                                              buttonActionCancel: (() -> Unit)? = null) {
            checkForConnectivity(
                    isNotConnected = {
                        activity.showNoInternetErrorWithRetryDialog(
                                error = noInternetError,
                                buttonActionRetry = buttonActionRetry,
                                buttonActionCancel = buttonActionCancel)
                    },
                    isConnected = isConnected)
        }
    }
}