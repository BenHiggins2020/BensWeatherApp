package com.ben.bensweatherapp

import android.util.Log
import com.ben.bensweatherapp.data.api.IconApi
import com.ben.bensweatherapp.data.api.WeatherApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

object AppModule {

    fun weatherApi(): WeatherApi {
        Log.d("AppModule","calling retrofit instance for weather api")
        val factory = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com")
            .addConverterFactory(MoshiConverterFactory.create(factory))
            .build()
            .create()

    }

    fun getIconApi(): IconApi {
        val factory = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl("https://openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create(factory))
            .build()
            .create()
    }
}