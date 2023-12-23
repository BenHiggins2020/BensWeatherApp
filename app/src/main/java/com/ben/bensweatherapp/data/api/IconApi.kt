package com.ben.bensweatherapp.data.api

import android.widget.ImageView
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IconApi {

    @GET("/img/wn/{icon}@2x.png")
    suspend fun getIcon(
        @Path("icon") icon:String
    ):ResponseBody
}