package com.example.weatherapp.basemodule.base.platform

import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.base.common.IApplicationContext
import com.example.weatherapp.basemodule.base.data.local.LocalDataSource
import com.example.weatherapp.basemodule.base.data.model.BaseResponse
import com.example.weatherapp.basemodule.base.data.remote.IRemoteDataSource
import com.example.weatherapp.basemodule.utils.HttpUtils
import com.example.weatherapp.basemodule.utils.getKoinInstance
import com.google.gson.Gson
import kotlinx.coroutines.Deferred
import retrofit2.Response

abstract class BaseRepository<LocalData : LocalDataSource, RemoteData : IRemoteDataSource>
(val localDataSource: LocalData, val remoteDataSource: RemoteData) : IBaseRepository {

    protected val applicationContext by getKoinInstance<IApplicationContext>()
    protected val gson by getKoinInstance<Gson>()

    override suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return HttpUtils.safeApiCall(call)
    }

    override suspend fun <T : Any> safeDeferredApiCall(call: () -> Deferred<Response<T>>): Result<T> {
        return HttpUtils.safeDeferredApiCall(call)
    }

    suspend inline fun <reified InputType : Any, reified ResponseOfInputType : BaseResponse<InputType>, reified ReturnType : Any>
            safeApiCall(
        crossinline networkCall: suspend () -> Response<ResponseOfInputType>,
        crossinline successHandler: (InputType) -> ReturnType
    )
            : Result<ReturnType> {
        return HttpUtils.safeApiCall(networkCall, successHandler)
    }

    suspend inline fun <reified InputType : Any, reified ReturnType : Any> safeApiCall2(
        crossinline networkCall: suspend () -> Response<InputType>,
        crossinline successHandler: (InputType) -> ReturnType
    ): Result<ReturnType> {
        return HttpUtils.safeApiCall2(networkCall, successHandler)
    }

    suspend inline fun <reified InputType : Any, reified ResponseOfInputType : BaseResponse<InputType>, reified ReturnType : Any>
            safeApiCallBaseWithHeaders(
        crossinline networkCall: suspend () -> Response<ResponseOfInputType>,
        crossinline successHandler: (InputType, HashMap<String, String>) -> ReturnType
    )
            : Result<ReturnType> {
        return HttpUtils.safeApiCallBaseWithHeaders(networkCall, successHandler)
    }

    suspend inline fun <reified InputType : Any, reified ResponseOfInputType : InputType, reified ReturnType : Any>
            safeApiCallWithHeaders(
        crossinline networkCall: suspend () -> Response<ResponseOfInputType>,
        crossinline successHandler: (InputType, HashMap<String, String>) -> ReturnType
    )
            : Result<ReturnType> {
        return HttpUtils.safeApiCallWithHeaders(networkCall, successHandler)
    }

}