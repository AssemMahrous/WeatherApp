package com.example.weatherapp.basemodule.base.platform

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.findNavController
import com.example.weatherapp.basemodule.utils.className
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getViewModel
import timber.log.Timber
import java.lang.ref.WeakReference

abstract class BaseFragment<ViewModel : BaseViewModel> : Fragment(), IBaseView<ViewModel> {
    /**
     * Class name for logging purposes.
     */
    protected val className by lazy { className() }

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
        loadingHandler = LoadingHandler.getInstance(requireActivity())
        Timber.d("Fragment '$className' has been created")
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        subscribeToViewModelUpdates()
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initGenericObservers()
    }

    override fun onResume() {
        super.onResume()
    }

    @CallSuper
    override fun onStop() {
        loadingHandler.stop()
        super.onStop()
    }

    @CallSuper
    override fun onDetach() {
        super.onDetach()
        Timber.d("Fragment '$className' has been detached")
    }

    override fun getCurrentActivity(): Activity = requireActivity()

    override fun getCurrentViewLifecycleOwner(): LifecycleOwner = viewLifecycleOwner

    /**
     * You can extend this method to safely subscribe to your view model's observables.
     */
    protected open fun subscribeToViewModelUpdates() =
        Unit // No-op, just a subscription placeholder

    /**
     * You can extend this method to safely initialize your views.
     */
    protected open fun initializeViews() = Unit // No-op, just an initialization placeholder

    /**
     * Exposes a result [value] with the given [key] to a previous fragment on the stack.
     * The [value] must be of a type that you can place in a Bundle (Parcelable).
     *
     * More info: https://developer.android.com/guide/navigation/navigation-programmatic#returning_a_result
     */
    protected fun <Type> exposeResultOnStack(key: String, value: Type) {
        findNavController().previousBackStackEntry?.savedStateHandle?.set(key, value)
    }

    /**
     * Observe results exposed via [exposeResultOnStack] that matches the given [key].
     * It's like having [onActivityResult] on fragments.
     */
    protected fun <Type> observeDestinationResult(key: String, onResult: (result: Type) -> Unit) {
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Type>(key)
            ?.observe(viewLifecycleOwner) { onResult.invoke(it) }
    }
}