package com.example.weatherapp.features.screens.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.basemodule.base.data.local.IPermissionsManager
import com.example.weatherapp.basemodule.base.platform.BaseFragment
import com.example.weatherapp.basemodule.utils.getKoinInstance
import com.example.weatherapp.basemodule.utils.viewbinding.viewBinding
import com.example.weatherapp.databinding.FragmentDailyBinding

class DailyFragment : BaseFragment<DailyViewModel>() {
    private val permissionsManager by getKoinInstance<IPermissionsManager>()
    var query: String? = null
    private val locationPermission =
        permissionsManager.registerForLocationPermission(this) { isGranted, _ ->
            viewModel.start(isGranted, query)
        }
    private val binding by viewBinding(FragmentDailyBinding::bind)
    private val adapter = DailyAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.weatherListRecyclerView.adapter = adapter

        binding.weatherListSwipeRefreshLayout.setOnRefreshListener {
            getData()
            binding.weatherListSwipeRefreshLayout.isRefreshing = false
        }

        binding.search.setOnClickListener {

        }
        getData()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.weatherView.observe(viewLifecycleOwner, {
            binding.cityName.text = it.cityName
            adapter.submitList(it.list)
        })
    }

    private fun getData() {
        if (permissionsManager.checkLocationPermission()) {
            viewModel.start(true, query)
        } else {
            locationPermission.launch()
        }
    }
}