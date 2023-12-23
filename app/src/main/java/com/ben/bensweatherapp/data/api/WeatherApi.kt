package com.ben.bensweatherapp.data.api

import com.ben.bensweatherapp.data.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {



    @GET(
    "/v1/forecast?hourly=temperature_2m,apparent_temperature,precipitation_probability,precipitation,weather_code,cloud_cover,cloud_cover_low,cloud_cover_mid,cloud_cover_high,wind_speed_10m,wind_direction_10m&daily=weather_code,temperature_2m_max,temperature_2m_min,daylight_duration,uv_index_max,precipitation_sum,precipitation_hours,precipitation_probability_max&temperature_unit=fahrenheit&wind_speed_unit=mph&precipitation_unit=inch"
    )
    suspend fun getWeatherData(
    @Query("latitude") latitude:String,
    @Query("longitude") longitude:String) : WeatherData


}