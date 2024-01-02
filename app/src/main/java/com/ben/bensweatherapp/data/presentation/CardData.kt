package com.ben.bensweatherapp.data.presentation

import android.graphics.Bitmap
import com.squareup.moshi.Json

//Daily Data instance

data class CardData(
    val daylight_duration: Any,
    val precipitation_hours: Double,
    val precipitation_probability_max: Int,
    val precipitation_sum: Double,
    val temperature_2m_max: Double,
    val temperature_2m_min: Any,
    val time: String,
    val uv_index_max: Any,
    val weather_code: Int,
    var icon:Bitmap?=null,
    var locationName: String? = null
)
