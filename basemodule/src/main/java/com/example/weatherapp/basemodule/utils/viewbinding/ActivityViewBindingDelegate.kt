package com.example.weatherapp.basemodule.utils.viewbinding

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 *  Usage: define a new property called binding inside the Activity class. The type of it will be
 *  the name of the xml file adding the word "Binding" at the end. Apply viewBinding delegation
 *  property and pass by parameter a callable reference with the method "inflate".
 *  Inside the onCreate method, when calling setContentView, binding.root call should be passed
 *
 *  Example:
 *  private val binding by viewBinding(ActivityNameBinding::inflate)
 *
 *  fun onCreate(...) {
 *      ...
 *      setContentView(binding.root)
 *      ...
 *  }
 *
 */
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T
) =
    lazy(LazyThreadSafetyMode.NONE) {
        bindingInflater.invoke(layoutInflater)
    }