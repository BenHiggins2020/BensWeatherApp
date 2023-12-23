package com.ben.bensweatherapp.data

data class DailyUnits(
    val daylight_duration: String,
    val precipitation_hours: String,
    val precipitation_probability_max: String,
    val precipitation_sum: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String,
    val time: String,
    val uv_index_max: String,
    val weather_code: String
)