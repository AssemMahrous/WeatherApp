package com.example.weatherapp.features.screens.main

import android.os.Bundle
import com.example.weatherapp.basemodule.base.platform.BaseActivity
import com.example.weatherapp.basemodule.utils.viewbinding.viewBinding
import com.example.weatherapp.databinding.ActivityMainBinding

class MainActivity : BaseActivity<MainViewModel>() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}