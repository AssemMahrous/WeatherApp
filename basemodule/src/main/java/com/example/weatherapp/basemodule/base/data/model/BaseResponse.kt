package com.example.weatherapp.basemodule.base.data.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
        @SerializedName("Status") val status: Int,
        @SerializedName("Data") val data: T?,
        @SerializedName("Errors") val errors: List<String>?,
        @SerializedName("ErrorCode") val errorCode: String?,
        @SerializedName("statusCode") val statusCode: Int,
        @SerializedName("message") val message: String?,
        @SerializedName("error") val error: String?
) {
    fun getResponseErrorCode() = errorCode ?: statusCode.toString()

    fun getResponseErrorMessage() = errors?.joinToString(separator = "\n") ?: "$error | $message"
}
