package com.example.weatherapp.basemodule.base.platform

import androidx.lifecycle.*
import com.example.weatherapp.basemodule.R
import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.base.data.model.ApplicationMessage
import com.example.weatherapp.basemodule.utils.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    /**
     * Class name for logging purposes.
     */
    protected val className by lazy { className() }

    val error = MutableLiveData<Event<Result.Error>>()
    val loading =
            MutableLiveData<Event<Loading>>().apply { value = Loading(LoadingStatus.Dismiss.All).toEvent() }
    var latestRetry: RetryOperation? = null
    val message = MutableLiveData<Event<ApplicationMessage.Message>>()

    init {
        Timber.d("ViewModel '$className' has been launched")
    }

    inline fun wrapBlockingOperation(
            loading: Loading = Loading(LoadingStatus.Show.FullScreenNotCancelable),
            noinline function: suspend () -> Any
    ): Job {
        this.loading.value = loading.toEvent()
        return viewModelScope.launch {
            try {
                if (isActive) function()
            } catch (throwable: Throwable) {
                latestRetry = RetryOperation(loading, function)
                handleError(throwable)
                Timber.e(throwable)
            } finally {
                hideLoading(loading)
            }
        }
    }

    inline fun <reified T : Any> wrapBlockingOperationLiveData(
            loading: Loading = Loading(LoadingStatus.Show.FullScreenNotCancelable),
            defaultOnError: T? = null,
            noinline function: suspend () -> T
    ): LiveData<T> {
        this.loading.value = loading.toEvent()
        return liveData {
            try {
                val value = function()
                emit(value)
            } catch (throwable: Throwable) {
                latestRetry = RetryOperation(loading, function)
                handleError(throwable)
                defaultOnError?.let { emit(it) }
                Timber.e(throwable)
            } finally {
                hideLoading(loading)
            }
        }
    }

    fun <T> handleResult(result: Result<T>,
                         skipAutoErrorHandlingIf: ((Result.Error) -> Boolean) = { false },
                         onError: ((Result.Error) -> Unit)? = null,
                         onSuccess: (Result.Success<T>) -> Unit) {
        when (result) {
            is Result.Success<T> -> onSuccess(result)
            is Result.Error -> {
                if (onError != null) onError(result)
                if (!skipAutoErrorHandlingIf(result)) {
                    throw result.exception
                } else {
                    Timber.e(result.exception)
                }
            }
        }.exhaustive
    }

    fun <T> handleResultLiveData(
            result: Result<T>,
            onError: ((Exception) -> Unit)? = null
    ): Result.Success<T> {
        when (result) {
            is Result.Success<T> -> {
                return result
            }
            is Result.Error -> {
                onError?.invoke(result.exception)
                throw result.exception
            }
        }.exhaustive
    }

    suspend fun <T> handleResultSuspend(
            result: Result<T>,
            onSuccess: suspend (Result.Success<T>) -> Unit
    ) {
        when (result) {
            is Result.Success<T> -> {
                onSuccess(result)
            }
            is Result.Error -> {
                throw result.exception
            }
        }.exhaustive
    }

    fun handleError(throwable: Throwable) = when (throwable) {
        is NetworkException -> handleNetworkError(throwable)
        is ApplicationException -> handleApplicationException(throwable)
        is CancellationException -> handleCancellationException(throwable)
        is LocalException -> handleLocalError(throwable)
        else -> TODO_IF_DEBUG()
    }.exhaustive

    private fun handleLocalError(throwable: LocalException) = when (throwable.type) {
        ErrorType.Local.ContentProvider.ReadContactError -> error.postValue(Event(Result.Error(throwable)))
        else -> error.postValue(Event(Result.Error(throwable)))
    }.exhaustive

    open fun handleNetworkError(networkException: NetworkException) =
            when (networkException.type) {
                ErrorType.Network.BadRequest -> handleBadRequest(networkException)
                // ErrorType.Network.Unexpected -> TODO()
                ErrorType.Network.NoInternetConnection -> error.postValue(Result.Error(networkException).toEvent())
                // ErrorType.Network.Unauthorized -> TODO()
                // ErrorType.Network.Forbidden -> TODO()
                // ErrorType.Network.NotFound -> TODO()
                // ErrorType.Network.MethodNotAllowed -> TODO()
                // ErrorType.Network.NotAcceptable -> TODO()
                // ErrorType.Network.PreconditionFailed -> TODO()
                // ErrorType.Network.UnsupportedMediaType -> TODO()
                // ErrorType.Network.InternalServerError -> TODO()
                else -> error.postValue(Result.Error(networkException).toEvent())
            }.exhaustive

    open fun handleBadRequest(networkException: NetworkException) =
            error.postValue(Result.Error(networkException).toEvent())

    open fun handleApplicationException(throwable: ApplicationException) {
        error.postValue(Result.Error(throwable).toEvent())
    }

    open fun handleParamsError(logMessage: String) {
        error.postValue(Event(Result.Error(ApplicationException(
                type = ErrorType.Unexpected,
                errorMessageRes = R.string.empty_state_wrong
        ))))
        logParamsError(logMessage)
    }

    open fun logParamsError(logMessage: String) {
        Timber.e(ApplicationException(
                type = ErrorType.Unexpected,
                errorMessage = logMessage
        ))
    }

    open fun handleCancellationException(cancelled: CancellationException) {
        error.postValue(Result.Error(ApplicationException(ErrorType.NotError, throwable = cancelled)).toEvent())
    }

    fun clearRetry() {
        latestRetry = null
    }

    fun retry() =
            latestRetry?.apply {
                wrapBlockingOperation(showLoading, function)
            }


    fun hideLoading(loading: Loading) {
        val dismissStatus = when (loading.status) {
            LoadingStatus.Show.FullScreenNotCancelable,
            LoadingStatus.Show.FullScreenCancelable -> LoadingStatus.Dismiss.FullScreen
            LoadingStatus.Show.Pagination -> LoadingStatus.Dismiss.Pagination
            LoadingStatus.Dismiss.Pagination,
            LoadingStatus.Dismiss.FullScreen,
            LoadingStatus.Dismiss.All -> LoadingStatus.Dismiss.All
        }.exhaustive
        this.loading.value = Loading(dismissStatus).toEvent()
    }

    override fun onCleared() {
        clearRetry()
        super.onCleared()
        Timber.d("ViewModel '$className' has been cleared")
    }
}