package com.ben.bensweatherapp.presentation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.ben.bensweatherapp.AppModule
import com.ben.bensweatherapp.data.WeatherData
import com.ben.bensweatherapp.data.presentation.CardData
import com.ben.bensweatherapp.mappers.toCardData
import com.ben.bensweatherapp.mappers.toWeatherIcon
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.ResponseBody

class MainActivity : ComponentActivity() {
    val TAG = "WeatherAppMainActivity"

    lateinit var apiData:WeatherData
    lateinit var cardData: CardData



    val DeepBlue =  Color(17,35,90)
    val LightBlue = Color(89,111,183)
    val Sage = Color(198,207,155)
    val Yellow = Color(246,236,169)
    val bgGradiant = Brush.verticalGradient(listOf(Sage,Yellow,LightBlue,DeepBlue).reversed())
    val cardGradiant = Brush.radialGradient(listOf(DeepBlue,LightBlue))

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.w(TAG,"onCreate called")


        val info = callWeatherApi().also {
            if(it.isCompleted){
                Log.w(TAG," job is compete")
                callIconApi()
            } else if(it.isCancelled) {
                Log.w(TAG," job is cancelled")

            }
            while (it.isActive){
                Log.d(TAG,"api call is active")
            }
            
        }



        setContent {

            MainView()

        }
    }


    @Composable
    fun MainView(){


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Yellow)

        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(bgGradiant),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                WeatherCard(
                    backgroundColor = LightBlue,
                    cardData = cardData
                )
                Spacer(modifier = Modifier.height(30.dp))
                HourlyDataRow(apiData.hourly)

            }

        }
    }

    fun callWeatherApi() = runBlocking {
        launch {
            apiData = AppModule.weatherApi().getWeatherData("52.2","43.2")
            cardData = apiData.daily.toCardData(0)
            Log.w(TAG,"Api call = ${apiData.daily.time.get(0)}")

        }
    }

    fun callIconApi() = runBlocking {
        launch {
            val res = AppModule.getIconApi().getIcon(cardData.weather_code.toWeatherIcon(cardData.weather_code))

                Log.e(TAG,"icon API call  ${res.contentType()}")
                val bitmap = BitmapFactory.decodeStream(res.byteStream())
                cardData.icon = bitmap


        }

    }

}


