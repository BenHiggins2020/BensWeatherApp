 package com.ben.bensweatherapp.presentation

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
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class HourlyDataRowViewModel : ViewModel() {
    var emptyHourlyData = Hourly(listOf(), listOf(0),listOf(0),listOf(0),listOf(0),listOf(0),listOf(0),listOf(0),listOf(),listOf(0),listOf(0),listOf(0))
    var liveHourlyData = MutableLiveData(emptyHourlyData)
    val TAG = HourlyDataRowViewModel::class.java.simpleName

    fun updateHourlyData(data:Hourly){
        Log.w(TAG," updating HourlyData with new Data  ==${liveHourlyData.value?.equals(data)}")

        liveHourlyData.value = data
    }


    @Composable
    fun HourlyDataRow(viewModel: HourlyDataRowViewModel){
        //Setup MutableLiveData
        val data by viewModel.liveHourlyData.observeAsState()

        //Work Begins
        val format: DateFormat = SimpleDateFormat("HH:mm:ss")

        val date = Date(System.currentTimeMillis())
        var time: String = format.format(date)
        var hours = time.get(0).toString() + time.get(1).toString()
        Log.e("MainActivity","BEN time = $time date = $date hours = $hours")

        if(hours.toInt() < 12){
            //AM
            time = hours + ":00 AM"

        } else if(hours.toInt() == 12) {
            time = hours + ":00 PM"
        }
        else{
            hours = (hours.toInt()-12).toString()
            time = hours + ":00 PM"
        }

        val index = data!!.time.indexOfFirst{
            Log.e("MainActivity,HourlyDataRow"," it = ${convertTime(it)[0]} vs mytime = $time")
            convertTime(it)[0].equals(time).also { Log.d("MainActivity","$it vs $time") }
        }
        Log.d("BEN","BEN time = $time index = $index")

        if(index != -1){
            //Restructure to only include current time
            try{
                    data!!.time = data!!.time.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.apparent_temperature = data!!.apparent_temperature.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.cloud_cover = data!!.cloud_cover.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.temperature_2m = data!!.temperature_2m.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.wind_direction_10m = data!!.wind_direction_10m.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.cloud_cover_high = data!!.cloud_cover_high.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.cloud_cover_mid = data!!.cloud_cover_mid.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.cloud_cover_low = data!!.cloud_cover_low.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.precipitation = data!!.precipitation.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.precipitation_probability = data!!.precipitation_probability.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.weather_code = data!!.weather_code.subList(fromIndex = index, toIndex = data!!.time.size-1)
                    data!!.wind_speed_10m = data!!.wind_speed_10m.subList(fromIndex = index, toIndex = data!!.time.size-1)



            } catch(e:Exception){
                Log.e("MainActivity","Exception = $e")
            }
        }




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
                        time = data?.time?.get(it) ?: System.currentTimeMillis().toString(),
                        wind_direction = data?.wind_direction_10m?.get(it) ?: 0,
                        wind_speed = data?.wind_speed_10m?.get(it) ?: 0,
                    )
                }
            })
    }

    fun convertTime(time:String):List<String>{
        val i = time.indexOfFirst{ it == 'T' }
        var date = time.slice(IntRange(0,i-1))
        var t = time.slice(IntRange(i+1,time.length-1))
        val thours = t[0].toString() + t[1].toString()
        val tminutes = t[3].toString() + t[4].toString()

//    Log.d("WeatherApp,convertTime", " t = $t $thours $tminutes, $time")

        if(thours.toInt() > 12){
            //PM
            t = (thours.toInt() - 12).toString() +":"+ tminutes + " PM"

        } else if (thours == "00"){
            t = "12"+":"+ tminutes + " AM"
        }
        else{
            t = thours +":"+ tminutes + " AM"
        }
        Log.d("WeatherApp,convertTime", " t = $t")

        return listOf(t,date)

    }
    fun getHourlyIconApi(weather_code:Int) = runBlocking {
        launch {
            val res = AppModule.getIconApi().getIcon(weather_code.toWeatherIcon(weather_code))

            Log.e("getHourlyIconApi","icon API call  ${res.contentType()}")
            val bitmap = BitmapFactory.decodeStream(res.byteStream()) as ImageBitmap
        }
    }

    @Composable
    fun miniHourlyCards(real_Feel:Any,real_Temp:Any,cloud_cover:Int,precipitation_probability:Int,time:String,wind_direction:Int,wind_speed:Any,weatherCode:Int?=null) {

        //time
        Log.d("WeatherApp","time = $time")
        val hrTime = convertTime(time)


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
                            text = "${hrTime[1]}",
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(5.dp),
                        )
                        Text(
                            text = "${hrTime[0]}",
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