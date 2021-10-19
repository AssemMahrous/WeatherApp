package com.example.weatherapp.basemodule.utils

import android.app.Activity
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.example.weatherapp.basemodule.R
import com.example.weatherapp.basemodule.Result
import com.example.weatherapp.basemodule.base.platform.IBaseView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object MessageUtils {

    @JvmStatic
    fun IBaseView<*>.showNoInternetErrorWithRetryDialog(error: Result.Error) {
        val errorMessage =
            getCurrentActivity().getString(
                error.exception.errorMessageRes
                    ?: R.string.message_error_no_internet
            )
        getCurrentActivity().showErrorDialog(
            message = errorMessage,
            positiveButtonAction = { viewModel.clearRetry() },
            negativeButtonText = getCurrentActivity().getString(R.string.action_retry),
            negativeButtonAction = { viewModel.retry() },
            onCancel = { viewModel.clearRetry() },
            onDismiss = { viewModel.clearRetry() }
        )
    }

    @JvmStatic
    fun Fragment.showNoInternetErrorWithRetryDialog(
        error: Result.Error,
        buttonActionRetry: (() -> Unit)?,
        buttonActionCancel: (() -> Unit)?
    ) {
        val errorMessage =
            getString(error.exception.errorMessageRes ?: R.string.message_error_no_internet)
        activity?.showErrorDialog(
            message = errorMessage,
            positiveButtonAction = buttonActionCancel,
            negativeButtonText = getString(R.string.action_retry),
            negativeButtonAction = buttonActionRetry
        )
    }

    @JvmStatic
    fun Activity.showNoInternetErrorWithRetryDialog(
        error: Result.Error,
        buttonActionRetry: (() -> Unit)?,
        buttonActionCancel: (() -> Unit)?
    ) {
        val errorMessage =
            getString(error.exception.errorMessageRes ?: R.string.message_error_no_internet)
        showErrorDialog(
            message = errorMessage,
            positiveButtonAction = buttonActionCancel,
            negativeButtonText = getString(R.string.action_retry),
            negativeButtonAction = buttonActionRetry
        )
    }

    @JvmStatic
    fun IBaseView<*>.showErrorDialog(error: Result.Error) {
        getCurrentActivity()
            .showErrorDialog(
                message = error.exception
                    .getErrorMessageString()
            )
    }

    @JvmStatic
    fun Activity.showErrorDialog(
        message: String,
        title: String? = null,
        positiveButtonText: String? = null,
        positiveButtonAction: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        negativeButtonAction: (() -> Unit)? = null,
        neutralButtonText: String? = null,
        neutralButtonAction: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null,
        cancelable: Boolean = true
    ) {
        showDialog(
            message = message,
            title = title ?: getString(R.string.title_error),
            positiveButtonText = positiveButtonText ?: getString(R.string.action_ok),
            positiveButtonAction = positiveButtonAction,
            negativeButtonText = negativeButtonText,
            negativeButtonAction = negativeButtonAction,
            neutralButtonText = neutralButtonText,
            neutralButtonAction = neutralButtonAction,
            onCancel = onCancel,
            onDismiss = onDismiss,
            cancelable = cancelable
        )
    }

    @JvmStatic
    fun Activity.showDialog(
        message: String,
        title: String? = null,
        positiveButtonText: String = getString(R.string.action_ok),
        positiveButtonAction: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        negativeButtonAction: (() -> Unit)? = null,
        neutralButtonText: String? = null,
        neutralButtonAction: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null,
        cancelable: Boolean = true
    ) {
        MaterialAlertDialogBuilder(this).apply {
            title?.let { setTitle(title) }
            setMessage(message)
            setCancelable(cancelable)
            setPositiveButton(positiveButtonText) { _, _ -> positiveButtonAction?.invoke() }
            negativeButtonText?.let { setNegativeButton(it) { _, _ -> negativeButtonAction?.invoke() } }
            neutralButtonText?.let { setNeutralButton(it) { _, _ -> neutralButtonAction?.invoke() } }
            onCancel?.let { action -> setOnCancelListener { action() } }
            onDismiss?.let { action -> setOnDismissListener { action() } }
        }.show()
    }

    @JvmStatic
    fun Fragment.showDialog(
        message: String,
        title: String? = null,
        positiveButtonText: String = getString(R.string.action_ok),
        positiveButtonAction: (() -> Unit)? = null,
        negativeButtonText: String? = null,
        negativeButtonAction: (() -> Unit)? = null,
        neutralButtonText: String? = null,
        neutralButtonAction: (() -> Unit)? = null,
        onCancel: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null,
        cancelable: Boolean = true
    ) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            title?.let { setTitle(title) }
            setMessage(message)
            setCancelable(cancelable)
            setPositiveButton(positiveButtonText) { _, _ -> positiveButtonAction?.invoke() }
            negativeButtonText?.let { setNegativeButton(it) { _, _ -> negativeButtonAction?.invoke() } }
            neutralButtonText?.let { setNeutralButton(it) { _, _ -> neutralButtonAction?.invoke() } }
            onCancel?.let { action -> setOnCancelListener { action() } }
            onDismiss?.let { action -> setOnDismissListener { action() } }
        }.show()
    }
}