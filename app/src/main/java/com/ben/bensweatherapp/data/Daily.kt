package com.ben.bensweatherapp.data

import com.squareup.moshi.Json

data class Daily(
    @field:Json(name = "daylight_duration")
    val daylight_duration: List<Any>,
    @field:Json(name = "precipitation_hours")
    val precipitation_hours: List<Double>,
    @field:Json(name = "precipitation_probability_max")
    val precipitation_probability_max: List<Int>,
    @field:Json(name = "precipitation_sum")
    val precipitation_sum: List<Double>,
    @field:Json(name = "temperature_2m_max")
    val temperature_2m_max: List<Double>,
    @field:Json(name = "temperature_2m_min")
    val temperature_2m_min: List<Any>,
    @field:Json(name = "time")
    val time: List<String>,
    @field:Json(name = "uv_index_max")
    val uv_index_max: List<Any>,
    @field:Json(name = "weather_code")
    val weather_code: List<Int>
)