package com.example.weatherapp.features.screens.daily

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DailyViewItemBinding
import com.example.weatherapp.features.module.view.WeatherViewEntity

class DailyViewHolder(
    private val binding: DailyViewItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: WeatherViewEntity) {
        binding.dayName.text = entity.dayName
        binding.temp.text = entity.temp
        binding.monthName.text = entity.monthName
        binding.descriptionValue.text = entity.description
        binding.windValue.text = entity.windSpeed
        binding.sunriseValue.text = entity.sunriseTime
        binding.sunsetValue.text = entity.sunsetTime
        binding.weatherCard.setOnClickListener {
            toggleVisibility(!binding.timeDivider.isVisible)
        }
    }

    fun toggleVisibility(visibility: Boolean) {
        binding.timeDivider.isVisible = visibility
        binding.conditionDivider.isVisible = visibility
        binding.sunriseTitle.isVisible = visibility
        binding.sunsetTitle.isVisible = visibility
        binding.sunriseValue.isVisible = visibility
        binding.sunsetValue.isVisible = visibility
        binding.conditionDivider.isVisible = visibility
        binding.descriptionTitle.isVisible = visibility
        binding.windTitle.isVisible = visibility
        binding.descriptionValue.isVisible = visibility
        binding.windValue.isVisible = visibility
    }

    companion object {
        fun create(parent: ViewGroup): DailyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.daily_view_item, parent, false)
            val binding = DailyViewItemBinding.bind(view)
            return DailyViewHolder(binding)
        }
    }
}
