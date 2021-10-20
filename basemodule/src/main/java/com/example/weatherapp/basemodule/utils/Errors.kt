package com.example.weatherapp.basemodule.utils

import com.example.weatherapp.basemodule.base.common.IApplicationResource
import timber.log.Timber

private val applicationResource by getKoinInstance<IApplicationResource>()

open class ApplicationException(
        open val type: ErrorType,
        open val errorMessage: String? = null,
        open val errorMessageRes: Int? = null,
        open val errorCode: String? = null,
        open val throwable: Throwable? = null
) : RuntimeException() {
    override val message: String?
        get() = "${getErrorMessageString()} | ${super.message.orEmpty()}"

    fun getErrorMessageString(): String {
        return errorMessage?.let { errorMessage }
                ?: errorMessageRes?.let { applicationResource.getString(errorMessageRes!!) }
                ?: "unexpected error"
    }
}

data class NetworkException(
        override val type: ErrorType,
        override val errorMessage: String? = null,
        override val errorMessageRes: Int? = null,
        override val errorCode: String? = null,
        override val throwable: Throwable? = null
) : ApplicationException(type, errorMessage, errorMessageRes, errorCode, throwable)

data class LocalException(
        override val type: ErrorType,
        override val errorMessage: String? = null,
        override val errorMessageRes: Int? = null,
        override val errorCode: String? = null,
        override val throwable: Throwable? = null
) : ApplicationException(type, errorMessage, errorMessageRes, errorCode, throwable)

data class LogException(override val message: String? = null,
                        override val cause: Throwable? = null) : RuntimeException(message, cause) {
    companion object {
        @JvmStatic
        fun log(message: String) = Timber.e(LogException("Custom: $message"))
    }
}

sealed class ErrorType {
    sealed class Network(val Code: Int) : ErrorType() {
        object Unexpected : Network(-1)
        object NoInternetConnection : Network(-2)

        object BadRequest : Network(400)
        object Unauthorized : Network(401)
        object Forbidden : Network(403)
        object NotFound : Network(404)
        object MethodNotAllowed : Network(405)
        object NotAcceptable : Network(406)
        object PreconditionFailed : Network(412)
        object UnsupportedMediaType : Network(415)
        object InternalServerError : Network(500)

        companion object {
            fun getNetworkApplicationException(code: Int, errorMessage: String?, errorCode: String?): NetworkException {
                return when (code) {
                    BadRequest.Code -> NetworkException(type = BadRequest, errorMessage = errorMessage, errorCode = errorCode)
                    Unauthorized.Code -> NetworkException(type = Unauthorized, errorMessage = errorMessage, errorCode = errorCode)
                    Forbidden.Code -> NetworkException(type = Forbidden, errorMessage = errorMessage, errorCode = errorCode)
                    NotFound.Code -> NetworkException(type = NotFound, errorMessage = errorMessage, errorCode = errorCode)
                    MethodNotAllowed.Code -> NetworkException(type = MethodNotAllowed, errorMessage = errorMessage, errorCode = errorCode)
                    NotAcceptable.Code -> NetworkException(type = NotAcceptable, errorMessage = errorMessage, errorCode = errorCode)
                    PreconditionFailed.Code -> NetworkException(type = PreconditionFailed, errorMessage = errorMessage, errorCode = errorCode)
                    UnsupportedMediaType.Code -> NetworkException(type = UnsupportedMediaType, errorMessage = errorMessage, errorCode = errorCode)
                    InternalServerError.Code -> NetworkException(type = InternalServerError, errorMessage = errorMessage, errorCode = errorCode)
                    else -> NetworkException(type = Unexpected, errorMessage = errorMessage, errorCode = errorCode)
                }.exhaustive
            }
        }
    }

    sealed class Local : ErrorType() {
        sealed class ContentProvider : Local() {
            object ReadContactError : ContentProvider()
        }
        sealed class Validation : Local() {
            object validationError : Validation()
        }
    }

    object Unexpected : ErrorType()
    object UserError : ErrorType()
    object Firebase : ErrorType()
    object NotError : ErrorType()

    data class Validation(val errors: HashMap<Int, Int?> = hashMapOf()) : ErrorType()
    data class BusinessValidation(val errorId: Int) : ErrorType()
}
