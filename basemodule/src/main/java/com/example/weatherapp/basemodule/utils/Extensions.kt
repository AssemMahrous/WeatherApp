package com.example.weatherapp.basemodule.utils

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.basemodule.BuildConfig
import com.example.weatherapp.basemodule.base.data.model.ApplicationMessage
import com.example.weatherapp.basemodule.base.di.AppConfigurationModule.isDebug
import com.google.android.material.datepicker.CalendarConstraints
import okhttp3.Headers
import okhttp3.internal.toHeaderList
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.core.qualifier.Qualifier
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
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

fun getDistance(lng1: Double, lat1: Double, lng2: Double, lat2: Double): Double {
    val df = DecimalFormat("#.##")
    val earthRadius = 3958.75
    val latDiff = Math.toRadians(lat2 - lat1)
    val lngDiff = Math.toRadians(lng2 - lng1)
    val a = sin(latDiff / 2) * sin(latDiff / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(lngDiff / 2) * sin(lngDiff / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance = earthRadius * c

    val meterConversion = 1609

    return df.format(distance * meterConversion).toDouble()
}

fun limitRange(): CalendarConstraints.Builder {
    val constraintsBuilderRange = CalendarConstraints.Builder()
    val calendarStart = Calendar.getInstance()
    val calendarEnd = Calendar.getInstance()
    val year = calendarStart.get(Calendar.YEAR)
    val startMonth = calendarStart.get(Calendar.MONTH)
    val startDate = calendarStart.get(Calendar.DAY_OF_MONTH)
    val endMonth = startMonth + 1
    val endDate = startDate + 10
    calendarStart[year, startMonth] = startDate
    calendarEnd[year, endMonth] = endDate
    val minDate = calendarStart.timeInMillis
    val maxDate = calendarEnd.timeInMillis
    constraintsBuilderRange.setStart(minDate)
    constraintsBuilderRange.setEnd(maxDate)
    constraintsBuilderRange.setValidator(RangeValidator(minDate, maxDate))
    return constraintsBuilderRange
}

internal class RangeValidator : CalendarConstraints.DateValidator {
    var minDate: Long
    var maxDate: Long

    constructor(minDate: Long, maxDate: Long) {
        this.minDate = minDate
        this.maxDate = maxDate
    }

    constructor(parcel: Parcel) {
        minDate = parcel.readLong()
        maxDate = parcel.readLong()
    }

    override fun isValid(date: Long): Boolean {
        return !(minDate > date || maxDate < date)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(minDate)
        dest.writeLong(maxDate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RangeValidator?> =
            object : Parcelable.Creator<RangeValidator?> {
                override fun createFromParcel(parcel: Parcel): RangeValidator? {
                    return RangeValidator(parcel)
                }

                override fun newArray(size: Int): Array<RangeValidator?> {
                    return arrayOfNulls(size)
                }
            }
    }
}

fun convertLongToTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("yyyy.MM.dd")
    return format.format(date)
}

fun currentTimeToLong(): Long {
    return System.currentTimeMillis()
}
