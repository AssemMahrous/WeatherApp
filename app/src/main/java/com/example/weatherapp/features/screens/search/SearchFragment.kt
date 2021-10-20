package com.example.weatherapp.features.screens.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.example.weatherapp.R
import com.example.weatherapp.basemodule.base.platform.BaseBottomSheetDialogFragment
import com.example.weatherapp.basemodule.utils.setNavigationResult
import com.example.weatherapp.basemodule.utils.viewbinding.viewBinding
import com.example.weatherapp.databinding.FragmentSearchBinding


class SearchFragment : BaseBottomSheetDialogFragment<SearchViewModel>() {
    private val binding by viewBinding(FragmentSearchBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            binding.continueButton.isEnabled = text.toString().trim().isNotEmpty()
        }

        binding.continueButton.setOnClickListener {
            setNavigationResult(REQUEST_KEY_SEARCH, binding.searchEditText.text.toString().trim())
            dismiss()
        }
    }

    companion object {
        const val REQUEST_KEY_SEARCH = "keySearch"
    }
}