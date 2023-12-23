package com.ben.bensweatherapp.data

import com.squareup.moshi.Json

data class Hourly(
    val apparent_temperature: List<Any>,
    val cloud_cover: List<Int>,
    val cloud_cover_high: List<Int>,
    val cloud_cover_low: List<Int>,
    val cloud_cover_mid: List<Int>,
    val precipitation: List<Any>,
    val precipitation_probability: List<Int>,
    val temperature_2m: List<Any>,
    val time: List<String>,
    val weather_code: List<Int>,
    val wind_direction_10m: List<Int>,
    val wind_speed_10m: List<Any>
)