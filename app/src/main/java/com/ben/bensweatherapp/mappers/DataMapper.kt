package com.ben.bensweatherapp.mappers

import com.ben.bensweatherapp.data.Daily
import com.ben.bensweatherapp.data.presentation.CardData

fun Daily.toCardData(day:Int):CardData{
    return CardData(
        daylight_duration = daylight_duration[day],
        precipitation_hours = precipitation_hours[day],
        precipitation_probability_max = precipitation_probability_max[day],
        precipitation_sum = precipitation_sum[day],
        temperature_2m_max = temperature_2m_max[day],
        temperature_2m_min = temperature_2m_min[day],
        time = time[day],
        uv_index_max = uv_index_max[day],
        weather_code = weather_code[day]
    )

}