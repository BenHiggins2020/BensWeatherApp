package com.ben.bensweatherapp.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ben.bensweatherapp.data.presentation.CardData
import java.lang.Double
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.math.roundToInt

class WeatherCardViewModel: ViewModel() {
    val time = LocalDate.now().toString()

    private val emptyCard=CardData(0,0.0,0,0.0,0.0,0,time,0,0)
    var liveCardData = MutableLiveData(emptyCard)

    private val TAG = WeatherCardViewModel::class.java.simpleName

    fun updateCardData(newCardData: CardData){
        Log.w(TAG," updating CardData with new Data ${liveCardData.value?.equals(newCardData)}")
        liveCardData.value = newCardData
        Log.w(TAG," updatED CardData with new Data ${liveCardData.value?.equals(newCardData)}")

    }

    @Composable
    fun WeatherCard(
        modifier: Modifier?=null,
        backgroundColor: Color,
        viewModel: WeatherCardViewModel,
        callback: () -> Unit,

    ){
        val cardData by viewModel.liveCardData.observeAsState()

        Log.e(TAG,"cardData = emptyCard? =  ${cardData?.equals(emptyCard)}")


        Log.e(TAG,"card data time = $time")
        var date:LocalDate
        try{
            date = LocalDate.parse(cardData?.time)

        }catch(e:java.lang.Exception){
            Log.e(TAG,"ERROR trying to create date $e")
            date = LocalDate.now()
        }

        val DOW = (date.dayOfWeek).toString().lowercase().replaceFirstChar({it -> it.uppercase()})
        val month = date.month.toString().lowercase().replaceFirstChar({it -> it.uppercase()})
        val DOM = date.dayOfMonth.toString().lowercase().replaceFirstChar({it -> it.uppercase()})


        var daylights = Double.valueOf (cardData?.daylight_duration.toString())
        var daylighthrs = (daylights / 60 / 60).roundToInt()
        var bitmap by remember {
            mutableStateOf(cardData?.icon)
        }

        if(bitmap == null){
            bitmap = Bitmap.createBitmap(10,10,Bitmap.Config.ARGB_8888)
        }


        Card(
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .fillMaxHeight(.55f)
                .fillMaxWidth(.75f)
                .padding(top = 20.dp)
            ,
            backgroundColor = backgroundColor

        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp),

                ) {
                TextButton(
                    onClick = callback,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text= " ${DOW} ${month} ${DOM}  ",
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp

                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text= " ${cardData?.locationName} ",
                            color = Color.White,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp

                        )
                    }

                }
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(.55f)
                        .fillMaxHeight(.55f)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    content = {
                        items(1) {this
                            bottomData(title = "Temp. High", value = cardData?.temperature_2m_max!! , units ="°F" )
                            Spacer(modifier = Modifier.width(20.dp))
                            bottomData(title = "Temp. Low", value = cardData?.temperature_2m_min!! , units ="°F" )
                            Spacer(modifier = Modifier.width(20.dp))
                            bottomData(title = "Precip. Chance", value = cardData?.precipitation_probability_max!! , units ="%" )
                            Spacer(modifier = Modifier.width(20.dp))
                            bottomData(title = "Precip. Total", value = cardData?.precipitation_sum!! , units ="in" )
                            Spacer(modifier = Modifier.width(20.dp))
                            bottomData(title = "UV index (max)", value = cardData?.uv_index_max!! , units ="" )
                            Spacer(modifier = Modifier.width(20.dp))
                            bottomData(title = "Hours or Daylight", value = daylighthrs , units ="hrs" )
                            Spacer(modifier = Modifier.width(20.dp))



                        }
                    }

                )



            }

        }


    }
    @Composable
    fun bottomData(title:String, value:Any,units:String,){
        Column(
            modifier = Modifier
                .fillMaxHeight(1f)
                .fillMaxWidth(.33f)
//            .background(Color.DarkGray)
                .padding(top = 4.dp)

            ,
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Text(
                text = "$title",
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,



                )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "${value} ${units}",
                color = Color.White,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            )
        }
    }

}