package com.example.weatherapp.basemodule.base.data.model

import android.content.Context
import androidx.annotation.StringRes
import timber.log.Timber

sealed class ApplicationMessage {
    data class Message(val messageType: MessageType,
                       val viewType: ViewType,
                       val actions: List<MessageAction> = listOf())

    sealed class MessageType(open val title: String? = null,
                             @StringRes open val titleRes: Int? = null,
                             open val message: String? = null,
                             @StringRes open val messageRes: Int? = null) {
        fun getMessageString(context: Context): String {
            return message?.let { message }
                    ?: messageRes?.let { context.getString(messageRes!!) }
                    ?: run {
                        Timber.e("Empty Message Body")
                        ""
                    }
        }

        fun getTitleString(context: Context): String? {
            return title?.let { title }
                    ?: titleRes?.let { context.getString(titleRes!!) }
                    ?: run {
                        Timber.e("Empty Message Title")
                        null
                    }
        }

        data class Success(
                override val title: String? = null,
                override val titleRes: Int? = null,
                override val message: String? = null,
                override val messageRes: Int? = null
        ) : MessageType(title, titleRes, message, messageRes)

        data class Error(
                override val title: String? = null,
                override val titleRes: Int? = null,
                override val message: String? = null,
                override val messageRes: Int? = null
        ) : MessageType(title, titleRes, message, messageRes)

        data class Info(
                override val title: String? = null,
                override val titleRes: Int? = null,
                override val message: String? = null,
                override val messageRes: Int? = null
        ) : MessageType(title, titleRes, message, messageRes)
    }

    sealed class ViewType(open val cancelable: Boolean = true) {
        data class Dialog(override val cancelable: Boolean = true) : ViewType(cancelable)
        data class TopAlert(override val cancelable: Boolean = true) : ViewType(cancelable)
        data class Toast(override val cancelable: Boolean = true) : ViewType(cancelable)
    }

    sealed class MessageAction(open val actionText: String? = null,
                               @StringRes open val actionTextRes: Int? = null,
                               open val method: (() -> Unit)? = null) {
        fun getActionString(context: Context): String? {
            return actionText.let { actionText }
                    ?: actionTextRes?.let { context.getString(actionTextRes!!) }
        }

        data class Positive(
                override val actionText: String? = null,
                override val actionTextRes: Int? = null,
                override val method: (() -> Unit)? = null
        ) : MessageAction(actionText, actionTextRes, method)

        data class Negative(
                override val actionText: String? = null,
                override val actionTextRes: Int? = null,
                override val method: (() -> Unit)? = null
        ) : MessageAction(actionText, actionTextRes, method)

        data class Neutral(
                override val actionText: String? = null,
                override val actionTextRes: Int? = null,
                override val method: (() -> Unit)? = null
        ) : MessageAction(actionText, actionTextRes, method)
    }
}
