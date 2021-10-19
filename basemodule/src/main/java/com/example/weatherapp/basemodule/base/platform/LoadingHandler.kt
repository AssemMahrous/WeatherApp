package com.example.weatherapp.basemodule.base.platform

import android.app.Activity
import android.app.Dialog
import com.bumptech.glide.Glide
import com.example.weatherapp.basemodule.R
import kotlinx.android.synthetic.main.progress_dialog_custom.*
import java.lang.ref.WeakReference

class LoadingHandler private constructor(val activity: WeakReference<Activity>) {
    private lateinit var progressDialog: WeakReference<Dialog>

    init {
        if (!::progressDialog.isInitialized) {
            createDialog()
        }
    }

    private fun createDialog() {
        activity.get()?.let {
            progressDialog = WeakReference(Dialog(
                    it
            ).apply {
                setContentView(R.layout.progress_dialog_custom)
                Glide.with(it)
                        .load(R.drawable.loading)
                        .into(gif_progress_bar_image)
                setCancelable(false)
            })
        }
    }

    fun showLoading() {
        if (::progressDialog.isInitialized) {
            progressDialog.get() ?: run { createDialog() }
            progressDialog.get()?.run { if (!isShowing) show() }
        }
    }

    fun hideLoading() {
        if (::progressDialog.isInitialized) progressDialog.get()?.run { dismiss() }
    }

    fun stop() {
        if (::progressDialog.isInitialized) progressDialog.get()?.run { if (isShowing) cancel() }
    }

    companion object {
        @JvmStatic
        fun getInstance(activity: Activity): LoadingHandler {
            return LoadingHandler(WeakReference(activity))
        }
    }
}