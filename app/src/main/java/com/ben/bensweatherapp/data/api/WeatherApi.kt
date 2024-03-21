package com.ben.bensweatherapp.data.api

import com.ben.bensweatherapp.data.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {


//https://api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41&current=temperature_2m,apparent_temperature,precipitation,rain&hourly=temperature_2m
    @GET(
    "/v1/forecast?" +
            "current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,showers,snowfall,weather_code,wind_speed_10m,wind_direction_10m,wind_gusts_10m" +
            "&hourly=temperature_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,cloud_cover,cloud_cover_low,cloud_cover_mid,cloud_cover_high,wind_speed_10m,wind_direction_10m" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,daylight_duration,uv_index_max,precipitation_sum,precipitation_hours,precipitation_probability_max&temperature_unit=fahrenheit&wind_speed_unit=mph&precipitation_unit=inch" +
            "&past_days=1"
    )
    suspend fun getWeatherData(
    @Query("latitude") latitude:String,
    @Query("longitude") longitude:String) : WeatherData


}