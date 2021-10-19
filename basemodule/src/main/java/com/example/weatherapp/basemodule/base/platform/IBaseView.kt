package com.example.weatherapp.basemodule.base.platform

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.base.data.model.ApplicationMessage
import com.example.weatherapp.basemodule.utils.*
import com.example.weatherapp.basemodule.utils.MessageUtils.showErrorDialog
import com.example.weatherapp.basemodule.utils.MessageUtils.showNoInternetErrorWithRetryDialog
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

interface IBaseView<ViewModel : BaseViewModel> {
    val viewModel: ViewModel

    val sharedViewModels: MutableList<WeakReference<BaseViewModel>>

    val loadingHandler: LoadingHandler

    fun getCurrentActivity(): Activity

    fun getCurrentViewLifecycleOwner(): LifecycleOwner

    fun getViewModelStoreOwner(): ViewModelStoreOwner = this as ViewModelStoreOwner

    @Suppress("UNCHECKED_CAST")
    fun viewModelClass(): KClass<ViewModel> {
        // dirty hack to get generic type https://stackoverflow.com/a/1901275/719212
        return ((javaClass.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<ViewModel>).kotlin
    }

    fun initGenericObservers() {
        initLoading(viewModel)
        initError(viewModel)
        initMessage(viewModel)
        sharedViewModels
            .asSequence()
            .filter { it.get() != null }
            .map { it.get()!! }
            .forEach {
                initLoading(it)
                initError(it)
                initMessage(viewModel)
            }
    }

    fun initLoading(vm: BaseViewModel) {
        vm.loading.observe(getCurrentViewLifecycleOwner(), EventObserver {
            when (it.status) {
                is LoadingStatus.Show -> showLoading(it)
                is LoadingStatus.Dismiss -> hideLoading(it)
            }.exhaustive
        })
    }

    fun initError(vm: BaseViewModel) {
        vm.error.observe(getCurrentViewLifecycleOwner(), EventObserver {
            hideLoading(Loading(LoadingStatus.Dismiss.All))
            showError(it)
        })
    }

    fun initMessage(vm: BaseViewModel) {
        vm.message.observe(getCurrentViewLifecycleOwner(), EventObserver { message ->
            showMessage(message)
        })
    }

    fun showMessage(message: ApplicationMessage.Message) {
        val viewType = message.viewType
        val messageType = message.messageType
        val actions = message.actions
        when (messageType) {
            is ApplicationMessage.MessageType.Success -> TODO_IF_DEBUG()
            is ApplicationMessage.MessageType.Info, // todo handle info status
            is ApplicationMessage.MessageType.Error -> {
                when (viewType) {
                    is ApplicationMessage.ViewType.Dialog -> {
                        val (positiveAction,
                            negativeAction,
                            neutralAction) = actions.getActions()

                        getCurrentActivity().showErrorDialog(
                            message = messageType.getMessageString(getCurrentActivity()),
                            title = messageType.getTitleString(getCurrentActivity()),
                            positiveButtonText = positiveAction?.getActionString(getCurrentActivity()),
                            positiveButtonAction = positiveAction?.method,
                            negativeButtonText = negativeAction?.getActionString(getCurrentActivity()),
                            negativeButtonAction = negativeAction?.method,
                            neutralButtonText = neutralAction?.getActionString(getCurrentActivity()),
                            neutralButtonAction = negativeAction?.method,
                            cancelable = viewType.cancelable
                        )
                    }
                    is ApplicationMessage.ViewType.TopAlert -> TODO_IF_DEBUG()
                    is ApplicationMessage.ViewType.Toast -> TODO_IF_DEBUG()
                }.exhaustive
            }
        }.exhaustive
    }

    fun showError(error: Result.Error) {
        isNotDebug {
            when (error.exception.type) {
                ErrorType.Network.NoInternetConnection -> showNoInternetErrorWithRetryDialog(error)
                else -> showErrorDialog(error)
            }
            return
        }
        isDebug {
            when (error.exception.type) {
                ErrorType.Network.NoInternetConnection -> showNoInternetErrorWithRetryDialog(error)
                ErrorType.Network.NotFound,
                ErrorType.Network.BadRequest -> showErrorDialog(error)
                ErrorType.Network.Unauthorized -> TODO_IF_DEBUG()
                ErrorType.Network.Unexpected -> TODO_IF_DEBUG()
                ErrorType.Network.Forbidden -> TODO_IF_DEBUG()
                ErrorType.Network.MethodNotAllowed -> TODO_IF_DEBUG()
                ErrorType.Network.NotAcceptable -> TODO_IF_DEBUG()
                ErrorType.Network.PreconditionFailed -> TODO_IF_DEBUG()
                ErrorType.Network.InternalServerError -> TODO_IF_DEBUG()
                ErrorType.Network.UnsupportedMediaType -> TODO_IF_DEBUG()
                ErrorType.Local.ContentProvider.ReadContactError -> showErrorDialog(error)
                ErrorType.Unexpected -> TODO_IF_DEBUG()
                ErrorType.UserError -> TODO_IF_DEBUG()
                ErrorType.Firebase -> TODO_IF_DEBUG()
                ErrorType.NotError -> TODO_IF_DEBUG()
                is ErrorType.Validation -> TODO_IF_DEBUG()
                is ErrorType.BusinessValidation -> TODO_IF_DEBUG()
            }.exhaustive
        }
    }

    fun hideLoading(loading: Loading) {
        // todo handle multiple cases of hide loading (full, list, etc...)
        loadingHandler.hideLoading()
    }

    fun showLoading(loading: Loading) {
        // todo handle multiple cases of show loading (full, list, etc...)
        hideKeyboard()
        loadingHandler.showLoading()
    }

    fun hideKeyboard() {
        if (this@IBaseView is Fragment) this.closeKeyboard()
        else getCurrentActivity().closeKeyboard()
    }
}