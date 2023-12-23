package com.ben.bensweatherapp.data

import com.squareup.moshi.Json

data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val timezone: String,
    val timezone_abbreviation: String,
    val utc_offset_seconds: Int,
    val elevation: Double,
    @field:Json(name = "hourly_units")
    val hourly_units: HourlyUnits,
    @field:Json(name = "hourly")
    val hourly: Hourly,
    @field:Json(name = "daily_units")
    val daily_units: DailyUnits,
    @field:Json(name = "daily")
    val daily: Daily,






)