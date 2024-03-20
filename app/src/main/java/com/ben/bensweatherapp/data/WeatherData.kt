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
    @field:Json(name="current_units")
    val current_units:CurrentUnits,
    @field:Json(name="current")
    val current:Current,
    @field:Json(name = "hourly_units")
    val hourly_units: HourlyUnits,
    @field:Json(name = "hourly")
    val hourly: Hourly,
    @field:Json(name = "daily_units")
    val daily_units: DailyUnits,
    @field:Json(name = "daily")
    val daily: Daily,

)

data class CurrentUnits(
    val time: String,
    val interval:String,
    val temperature_2m:String,
    val apparent_temperature:String,
    val precipitation:String,
    val rain: String,
    val showers: String,
    val snowfall: String,
    val weather_code: String,
    val wind_speed_10m: String,
    val wind_direction_10m:String,
    val wind_gusts_10m: String
)

data class Current(
    val time: String,
    val interval:Int,
    val temperature_2m:Double,
    val relative_humidity_2m:String,
    val apparent_temperature: Double,
    val precipitation:Any,
    val rain:Any,
    val showers:Any,
    val snowfall:Any,
    val weather_code:Any,
    val wind_speed_10m:Any,
    val wind_direction_10m:Any,
    val wind_gusts_10m:Any
)