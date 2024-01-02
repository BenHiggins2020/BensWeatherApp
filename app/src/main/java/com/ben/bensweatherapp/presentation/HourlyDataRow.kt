package com.ben.bensweatherapp.presentation

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ben.bensweatherapp.data.Hourly


@Composable
fun HourlyDataRow(data:Hourly){
    Log.d("HourlyDataRow","data ${data.apparent_temperature.size}")

    LazyRow(
            Modifier
//            .background(Color.Cyan)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,

        content = {

        items(data.apparent_temperature.size){
            Log.d("WeatherApp","creating hourly data, @ $it ${data.time[it]}")
            miniHourlyCards(
                real_Feel = data.apparent_temperature[it],
                real_Temp = data.temperature_2m[it],
                cloud_cover = data.cloud_cover[it],
                precipitation_probability = data.precipitation_probability[it],
                time = data.time[it],
                wind_direction = data.wind_direction_10m[it],
                wind_speed = data.wind_speed_10m[it],
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

    Log.d("WeatherApp,convertTime", " t = $t $thours $tminutes, $time")

    if(thours.toInt() > 12){
        //PM
        t = (thours.toInt() - 12).toString() +":"+ tminutes + " PM"

    } else if (thours == "00"){
        t = "12"+":"+ tminutes + " AM"
    }
    else{
        t = thours +":"+ tminutes + " AM"
    }

    return listOf(t,date)

}

@Composable
fun miniHourlyCards(real_Feel:Any,real_Temp:Any,cloud_cover:Int,precipitation_probability:Int,time:String,wind_direction:Int,wind_speed:Any) {

    //time
    Log.d("WeatherApp","time = $time")
    val hrTime = convertTime(time)
    Box(
        modifier = Modifier
            .fillMaxHeight(.85f)
            .width(130.dp)
            .padding(start = 20.dp)
    ){
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