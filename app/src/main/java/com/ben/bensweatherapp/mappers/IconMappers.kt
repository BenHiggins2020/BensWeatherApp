package com.ben.bensweatherapp.mappers


fun Int.toWeatherIcon(weatherCode:Int):String{
    return when(weatherCode){
        0 -> "01d"//clear sky
        1 -> "02d"//Mainly clear
        2 -> "03d"// Partly Cloudy
        3 -> "04d"//Overcast
        45 -> "50d"//Foggy
        48 -> "50d"//rimeFog??
        51 -> "10d"//light Drizzle
        53 -> "10d"//moderate Drizzle
        55 -> "10d"//dense Drizzle
        56 -> "13d"//Freezing Drizzle
        57 -> "13d"//Freezing dense Drizzle
        61 -> "10d"//Rain ligh
        63 -> "10d"//Rain med
        65 -> "10d"//Rain Heavy
        66 -> "13d"//freezing rain light
        67 -> "13d"//freezing rain heavy
        71 -> "13d"//snow heavy
        73 -> "13d"//snow med
        75 -> "13d"//snow heavy
        77 -> "13d"//snow grains
        80 -> "09d"//Rain showers light
        81 -> "09d"//Rain showers moderate
        82 -> "09d"// rain showers heavy
        85 -> "13d"// snow showers light
        86 -> "13d"// snow showers heavy
        95 -> "11d"//Thunder storm light - med
        96 -> "11d"//thunder with light hail
        99 -> "11d"// thunder with heavy hail
        else -> "01d"
    }
}