package com.example.weatherapp.basemodule.base.platform

import android.app.Activity
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.example.weatherapp.basemodule.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getViewModel
import java.lang.ref.WeakReference

abstract class BaseBottomSheetDialogFragment<ViewModel : BaseViewModel> :
    BottomSheetDialogFragment(), IBaseView<ViewModel> {
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
        setStyle(DialogFragment.STYLE_NORMAL, R.style.UiKit_ThemeOverlay_BottomSheetDialog)
        loadingHandler = LoadingHandler.getInstance(requireActivity())
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initGenericObservers()
    }

    @CallSuper
    override fun onStop() {
        loadingHandler.stop()
        super.onStop()
    }

    override fun getCurrentActivity(): Activity = requireActivity()

    override fun getCurrentViewLifecycleOwner(): LifecycleOwner = viewLifecycleOwner
}
