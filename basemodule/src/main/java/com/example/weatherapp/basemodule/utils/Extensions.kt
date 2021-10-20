package com.example.weatherapp.basemodule.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.basemodule.BuildConfig
import com.example.weatherapp.basemodule.base.data.model.ApplicationMessage
import com.example.weatherapp.basemodule.base.di.AppConfigurationModule.isDebug
import okhttp3.Headers
import okhttp3.internal.toHeaderList
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.Qualifier
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.reflect.KClass

private val sdfWithSeconds = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    .apply { timeZone = TimeZone.getTimeZone("UTC") }
private val sdfWithoutSeconds = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
    .apply { timeZone = TimeZone.getTimeZone("UTC") }


inline fun isDebug(block: () -> Unit) {
    if (isDebug && BuildConfig.BUILD_TYPE.contains("debug", ignoreCase = true)) block()
}

inline fun isNotDebug(block: () -> Unit) {
    if (!isDebug || !BuildConfig.BUILD_TYPE.contains("debug", ignoreCase = true)) block()
}

fun <T : Any> Class<T>?.getKClass(): KClass<T>? = this?.kotlin

inline fun <reified T> getKoinInstance(qualifier: Qualifier? = null): Lazy<T> {
    return lazy {
        return@lazy object : KoinComponent {
            val value by inject<T>(qualifier)
        }.value
    }
}

val <T> T.exhaustive: T get() = this

fun <T> Fragment.setNavigationResult(key: String, value: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
}

inline fun <reified T> Fragment.getNavigationResult(key: String = "result") =
    findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)

fun Any.className(): String = this::class.java.simpleName

fun isMarshmallow() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

inline fun TODO_IF_DEBUG(reason: String? = null) = isDebug {
    throw reason?.let { throw NotImplementedError("An operation is not implemented: $it") }
        ?: throw NotImplementedError()
}

fun List<ApplicationMessage.MessageAction>.getActions()
        : Triple<ApplicationMessage.MessageAction?, ApplicationMessage.MessageAction?,
        ApplicationMessage.MessageAction?> {
    val positiveAction = firstOrNull { it is ApplicationMessage.MessageAction.Positive }
    val negativeAction = firstOrNull { it is ApplicationMessage.MessageAction.Negative }
    val neutralAction = firstOrNull { it is ApplicationMessage.MessageAction.Neutral }
    return Triple(positiveAction, negativeAction, neutralAction)
}

fun Fragment.closeKeyboard() {
    if (isAdded)
        (requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .run {
                if (!requireActivity().isFinishing) {
                    hideSoftInputFromWindow(this@closeKeyboard.view?.windowToken, 0)
                }
            }
}

fun Activity.closeKeyboard() {
    (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .run {
            var view: View? = currentFocus
            //If no view currently has focus, create a new one, just so we can grab a window token from it
            if (view == null) view = View(this@closeKeyboard)
            hideSoftInputFromWindow(view.windowToken, 0)
        }
}

fun Boolean?.isNullOrFalse() = this == null || !this

fun Date?.toStringFormat(): String? {
    return when {
        this != null -> sdfWithSeconds.format(this)
        else -> null
    }
}

fun String?.toDate(): Date? {
    return when {
        !this.isNullOrEmpty() -> try {
            sdfWithSeconds.parse(this)
        } catch (e: Exception) {
            sdfWithoutSeconds.parse(this)
        }
        else -> Date()
    }
}

fun <T : Any> T.toEvent() = Event(this)

fun Headers?.toHashMap() =
    hashMapOf<String, String>().apply {
        this@toHashMap?.toHeaderList()?.forEach { header ->
            this[header.name.utf8()] = header.value.utf8()
        }
    }

fun getDayNameFromDateFuture(days: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, days)
    val date = calendar.time
    val formatter = SimpleDateFormat("EEE", Locale.ENGLISH)
    return formatter.format(date)
}

fun getDate(days: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, days)
    val date = calendar.time
    val formatter = SimpleDateFormat("MMM d", Locale.ENGLISH)
    return formatter.format(date)
}

fun getTimeShortFormat(time: Long): String {
    val date = Date(time * 1000)
    val formatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    return formatter.format(date)
}

fun <T> MutableLiveData<T>.toLiveData(): LiveData<T> {
    return this
}

fun roundOffDecimal(number: Double): Double? {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(number).toDouble()
}
