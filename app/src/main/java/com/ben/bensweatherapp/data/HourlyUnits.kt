package com.ben.bensweatherapp.data

data class HourlyUnits(
    val apparent_temperature: String,
    val cloud_cover: String,
    val cloud_cover_high: String,
    val cloud_cover_low: String,
    val cloud_cover_mid: String,
    val precipitation: String,
    val precipitation_probability: String,
    val temperature_2m: String,
    val time: String,
    val weather_code: String,
    val wind_direction_10m: String,
    val wind_speed_10m: String
)