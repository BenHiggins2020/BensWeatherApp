package com.ben.bensweatherapp.data

import com.squareup.moshi.Json

data class Hourly(
    var apparent_temperature: List<Any>,
    var cloud_cover: List<Int>,
    var cloud_cover_high: List<Int>,
    var cloud_cover_low: List<Int>,
    var cloud_cover_mid: List<Int>,
    var precipitation: List<Any>,
    var precipitation_probability: List<Int>,
    var temperature_2m: List<Any>,
    var time: List<String>,
    var weather_code: List<Int>,
    var wind_direction_10m: List<Int>,
    var wind_speed_10m: List<Any>
)