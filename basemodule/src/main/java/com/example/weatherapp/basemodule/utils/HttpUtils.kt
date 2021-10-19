package com.example.weatherapp.basemodule.utils

import com.example.weatherapp.basemodule.R
import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.base.data.model.BaseResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

object HttpUtils {
    private val connectivityUtils by getKoinInstance<IConnectivityUtils>()
    private val gson by getKoinInstance<Gson>()
    private val noInternetError = Result.Error(
        NetworkException(
            type = ErrorType.Network.NoInternetConnection,
            errorMessageRes = R.string.message_error_no_internet
        )
    )
    private val unexpectedError =
        Result.Error(ApplicationException(type = ErrorType.Network.Unexpected))

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                // check internet connection
                if (connectivityUtils.isNetworkConnected.not()) return@withContext noInternetError

                // make api call
                val response = call()

                // check response and convert to result
                return@withContext handleResult(response)

            } catch (error: Throwable) {
                Timber.e(error)
                unexpectedError(error)
            }
        }
    }

    suspend inline fun <reified InputType : Any, reified ResponseOfInputType : BaseResponse<InputType>, reified ReturnType : Any>
            safeApiCall(
        crossinline networkCall: suspend () -> Response<ResponseOfInputType>,
        crossinline successHandler: (InputType) -> ReturnType
    ): Result<ReturnType> {
        return when (val result = safeApiCall { networkCall() }) {
            is Result.Success -> Result.Success(successHandler(result.data.data!!))
            is Result.Error -> result
        }.exhaustive
    }

    suspend inline fun <reified InputType : Any, reified ReturnType : Any> safeApiCall2(
        crossinline networkCall: suspend () -> Response<InputType>,
        crossinline successHandler: (InputType) -> ReturnType
    ): Result<ReturnType> {
        return when (val result = safeApiCall { networkCall() }) {
            is Result.Success -> Result.Success(successHandler(result.data))
            is Result.Error -> result
        }.exhaustive
    }

    suspend inline fun <reified InputType : Any, reified ResponseOfInputType : BaseResponse<InputType>, reified ReturnType : Any>
            safeApiCallBaseWithHeaders(
        crossinline networkCall: suspend () -> Response<ResponseOfInputType>,
        crossinline successHandler: (InputType, HashMap<String, String>) -> ReturnType
    ): Result<ReturnType> {
        var response: Response<ResponseOfInputType>? = null
        return when (val result = safeApiCall {
            response = networkCall()
            response!!
        }) {
            is Result.Success -> Result.Success(
                successHandler(
                    result.data.data!!,
                    response?.headers().toHashMap()
                )
            )
            is Result.Error -> result
        }.exhaustive
    }

    suspend inline fun <reified InputType : Any, reified ResponseOfInputType : InputType, reified ReturnType : Any>
            safeApiCallWithHeaders(
        crossinline networkCall: suspend () -> Response<ResponseOfInputType>,
        crossinline successHandler: (InputType, HashMap<String, String>) -> ReturnType
    ): Result<ReturnType> {
        var response: Response<ResponseOfInputType>? = null
        return when (val result = safeApiCall {
            response = networkCall()
            response!!
        }) {
            is Result.Success -> Result.Success(
                successHandler(
                    result.data,
                    response?.headers().toHashMap()
                )
            )
            is Result.Error -> result
        }.exhaustive
    }

    suspend fun <T : Any> safeDeferredApiCall(call: () -> Deferred<Response<T>>): Result<T> {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                // check internet connection
                if (connectivityUtils.isNetworkConnected.not()) return@withContext noInternetError

                // make api call
                val response = call().await()

                // check response and convert to result
                return@withContext handleResult(response)
            } catch (error: Throwable) {
                Timber.e(error)
                unexpectedError(error)
            }
        }
    }

    private fun unexpectedError(error: Throwable): Result.Error {
        return Result.Error(
            ApplicationException(
                throwable = error,
                type = ErrorType.Network.Unexpected
            )
        )
    }

    private fun <T : Any> handleResult(response: Response<T>): Result<T> {
        return when (val responseCode = response.code()) {
            in 1..399 -> Result.Success(response.body()!!, response.headers())
            else -> {
                val (errorCode, errorMessage) = getErrorMessage(response)
                val exception = ErrorType.Network
                    .getNetworkApplicationException(responseCode, errorMessage, errorCode)
                Result.Error(exception)
            }
        }
    }

    private fun <T> getErrorMessage(response: Response<T>): Pair<String?, String?> {
        val errorBody = response.errorBody()?.string()
        val baseResponse =
            gson.fromJson<BaseResponse<*>>(
                errorBody,
                object : TypeToken<BaseResponse<*>>() {}.type
            )
        return baseResponse.getResponseErrorCode() to baseResponse.getResponseErrorMessage()
    }
}