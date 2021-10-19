package com.example.weatherapp.basemodule.base.platform

import com.example.weatherapp.basemodule.Result
import kotlinx.coroutines.Deferred
import retrofit2.Response

interface IBaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T>
    suspend fun <T : Any> safeDeferredApiCall(call: () -> Deferred<Response<T>>): Result<T>
}