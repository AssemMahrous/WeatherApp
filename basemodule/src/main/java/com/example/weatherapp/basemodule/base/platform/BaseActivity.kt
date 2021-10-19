package com.example.weatherapp.basemodule.base.platform

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.example.weatherapp.basemodule.R
import com.example.weatherapp.basemodule.utils.MessageUtils.showDialog
import com.example.weatherapp.basemodule.utils.isMarshmallow
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getViewModel
import timber.log.Timber
import java.lang.ref.WeakReference

abstract class BaseActivity<ViewModel : BaseViewModel> : AppCompatActivity(), IBaseView<ViewModel> {


    override val viewModel: ViewModel by lazy {
        getKoin().getViewModel(
            owner = getViewModelStoreOwner(),
            clazz = viewModelClass()
        )
    }

    override val sharedViewModels = mutableListOf<WeakReference<BaseViewModel>>()

    override lateinit var loadingHandler: LoadingHandler

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadingHandler = LoadingHandler.getInstance(this)
        initGenericObservers()
        logIntent(intent)
        subscribeToViewModelUpdates()
    }

    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        logIntent(intent)
    }

    @CallSuper
    override fun onStop() {
        loadingHandler.stop()
        super.onStop()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
    }

    private fun logIntent(intent: Intent?) {
        intent?.let { Timber.v("$it - ${it.extras}") }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }


    override fun getCurrentActivity(): Activity = this

    override fun getCurrentViewLifecycleOwner(): LifecycleOwner = this

    protected fun showMessage(
        title: String?,
        message: String?,
        negativeMsg: String?,
        positiveMsg: String?
    ) {
        if (!message.isNullOrBlank() && !isDestroyed && !isFinishing) {
            showDialog(
                message = message,
                title = title,
                positiveButtonText = positiveMsg ?: getString(R.string.action_ok),
                cancelable = true,
                negativeButtonText = negativeMsg
            )
        }
    }

    protected fun showAlert(message: String?, retryAction: (() -> Unit)?) {
        if (!message.isNullOrBlank() && !isDestroyed && !isFinishing) {
            showDialog(
                message = message,
                title = getString(R.string.title_error),
                positiveButtonText = getString(R.string.action_ok),
                negativeButtonText = getString(R.string.action_retry),
                cancelable = true,
                negativeButtonAction = retryAction
            )
        }
    }

    protected fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (isMarshmallow()) {
            connectivityManager.activeNetwork != null
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected == true
        }
    }

    /**
     * You can extend this method to safely subscribe to your view model's observables.
     */
    protected open fun subscribeToViewModelUpdates() =
        Unit // No-op, just a subscription placeholder
}