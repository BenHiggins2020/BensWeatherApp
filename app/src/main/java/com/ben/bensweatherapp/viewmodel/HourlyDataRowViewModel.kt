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

        //Work Begins

        LazyRow(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,

            content = {

                items(data?.apparent_temperature!!.size){
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
            })
    }

    /*fun convertTime(time:String):List<String>{
        val i = time.indexOfFirst{ it == 'T' }
        var date = time.slice(IntRange(0,i-1))
        var t = time.slice(IntRange(i+1,time.length-1))
        val t_hours = t[0].toString() + t[1].toString()
        val t_minutes = t[3].toString() + t[4].toString()

        if(t_hours.toInt() > 12){
            //PM
            t = (t_hours.toInt() - 12).toString() +":"+ t_minutes + " PM"

        } else if (t_hours == "00"){
            t = "12"+":"+ t_minutes + " AM"
        }
        else{
            t = t_hours +":"+ t_minutes + " AM"
        }

        return listOf(t,date)

    }*/


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

        //time



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