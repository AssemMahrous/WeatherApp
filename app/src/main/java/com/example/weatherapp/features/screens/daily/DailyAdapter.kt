package com.example.weatherapp.features.screens.daily

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.weatherapp.features.module.view.WeatherViewEntity

class DailyAdapter : ListAdapter<WeatherViewEntity, DailyViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<WeatherViewEntity>() {
            override fun areItemsTheSame(oldItem: WeatherViewEntity, newItem: WeatherViewEntity): Boolean {
                return oldItem.dayName == newItem.dayName
            }

            override fun areContentsTheSame(oldItem: WeatherViewEntity, newItem: WeatherViewEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        return DailyViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}