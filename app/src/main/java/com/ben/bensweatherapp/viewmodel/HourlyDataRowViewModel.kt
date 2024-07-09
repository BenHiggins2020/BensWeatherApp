 package com.ben.bensweatherapp.viewmodel

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ben.bensweatherapp.AppModule
import com.ben.bensweatherapp.data.Hourly
import com.ben.bensweatherapp.mappers.toWeatherIcon
import com.ben.bensweatherapp.util.BensDataProviderUtil
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

 class HourlyDataRowViewModel @Inject constructor(
    private val dataUtilProvider: BensDataProviderUtil
): ViewModel() {
    var emptyHourlyData = Hourly(listOf(), listOf(0),listOf(0),listOf(0),listOf(0),listOf(0),listOf(0),listOf(0),listOf(),listOf(0),listOf(0),listOf(0))
    var liveHourlyData = MutableLiveData(emptyHourlyData)
    val TAG = HourlyDataRowViewModel::class.java.simpleName


    @Composable
    fun HourlyDataRow(viewModel: HourlyDataRowViewModel){



        //Setup MutableLiveData
        val data by viewModel.liveHourlyData.observeAsState()

        //create a list of values from Data, which start at the current hour
        val current_time = SimpleDateFormat("HH:mm").format(Date())
        val date = LocalDate.now()

        val hour = "${current_time.toString()[0]}${current_time.toString()[1]}"
        val targetTime = "${date}T$hour:00"



         var hourlyDataTime = dataUtilProvider.apiData.hourly.time.first()
         var dailyDataTime = dataUtilProvider.apiData.daily.time.first()
         var currentDataTime = dataUtilProvider.apiData.current.time.first()


        Log.e(TAG,"JANGIS CURRENT_TIME " +
                "$targetTime first " +
                "date = ${ data?.time?.first()}" +
                "hourly ${hourlyDataTime}" +
                "daily ${dailyDataTime}" +
                "current ${currentDataTime}")

        //Index of the current time!!

        val index = data?.time?.indexOfFirst {
            Log.d(TAG,"it = $it == $targetTime  (${it.equals(targetTime)})")
            it == targetTime
        }


        LazyRow(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,

            content = {

                items(data?.apparent_temperature!!.size){
                    if(it>=index!!){
                        Log.d("WeatherApp","creating hourly data, @ $it ${data?.time?.get(it)}")
                        miniHourlyCards(
                            real_Feel = data?.apparent_temperature?.get(it) ?: 0,
                            real_Temp = data?.temperature_2m?.get(it) ?: 0,
                            cloud_cover = data?.cloud_cover?.get(it) ?: 0,
                            precipitation_probability = data?.precipitation_probability?.get(it) ?: 0,
                            time = dataUtilProvider.createDate(data?.time?.get(it)),
                            wind_direction = data?.wind_direction_10m?.get(it) ?: 0,
                            wind_speed = data?.wind_speed_10m?.get(it) ?: 0,
                        )
                    }

                }
            })
    }



    @Composable
    fun miniHourlyCards(real_Feel:Any,
                        real_Temp:Any,
                        cloud_cover:Int,
                        precipitation_probability:Int,
                        time:Map<String,String>,
                        wind_direction:Int,
                        wind_speed:Any,
                        weatherCode:Int?=null
    ) {


        Box(
            modifier = Modifier
                .fillMaxHeight(.85f)
                .width(130.dp)
                .padding(start = 20.dp)
        ){
            Spacer(Modifier.width(10.dp))
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()

            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally),
                    backgroundColor = Color.White,
                ){
                    Column() {
                        Text(
                            text = "${time.get("DOW")} ${time.get("DOM")}",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp),
                        )
                        Text(
                            text = "${time["Time"]}",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp),
                        )
                        Text(
                            text = "$real_Temp °F ",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp),
                        )
                        Text(
                            text = "Feel: $real_Feel °F ",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp),
                        )
                        Text(
                            text = "Precip: $precipitation_probability %",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp),
                        )
                        Text(
                            text = "Wind: $wind_speed mph",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp),
                        )


                    }
                }

            }
        }


    }
}