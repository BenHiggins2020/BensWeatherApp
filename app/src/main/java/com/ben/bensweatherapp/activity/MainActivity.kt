package com.ben.bensweatherapp.activity

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.ben.bensweatherapp.data.WeatherData
import com.ben.bensweatherapp.data.presentation.CardData
import com.ben.bensweatherapp.presentation.HourlyDataRowViewModel
import com.ben.bensweatherapp.presentation.LocationSearchPanel
import com.ben.bensweatherapp.presentation.WeatherCard
import com.ben.bensweatherapp.presentation.WeatherCardViewModel
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import com.ben.bensweatherapp.util.BensDataProviderUtil

class MainActivity : ComponentActivity() {
    val TAG = "MainActivity"

    var time: Long = 0L
    var lat: Double? = 0.0
    var long: Double? = 0.0
    var locationName:String = ""


    val weatherCard = WeatherCardViewModel()
    val hourlyData = HourlyDataRowViewModel()

    val DeepBlue =  Color(17,35,90)
    val LightBlue = Color(89,111,183)
    val Sage = Color(198,207,155)
    val Yellow = Color(246,236,169)
    val bgGradiant = Brush.verticalGradient(listOf(Sage,Yellow,LightBlue,DeepBlue).reversed())
    val cardGradiant = Brush.radialGradient(listOf(DeepBlue,LightBlue))
    lateinit var dataProviderUtil: BensDataProviderUtil


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.w(TAG,"onCreate called")

        val newLong = -122.083922
        val newLat = 37.4220936
       dataProviderUtil = BensDataProviderUtil(applicationContext,this)


        try{
            dataProviderUtil.hourlyDataSubject.subscribe(getHourlyDataObserver())
            dataProviderUtil.cardDataSubject.subscribe(getWeatherCardObserver())

        }catch (e:Exception){
            e.printStackTrace()
        }
        Log.w(TAG,"using data provider to force on next...")
        dataProviderUtil.hourlyDataSubject.onNext(dataProviderUtil.apiData)
        dataProviderUtil.cardDataSubject.onNext(dataProviderUtil.cardData)

        setContent {
            MainView()
        }
    }

    private fun getWeatherCardObserver(): Observer<CardData> {
        return object: Observer<CardData> {
            override fun onSubscribe(d: Disposable) {
                Log.w(TAG,"On subscribe to disposable ")

            }

            override fun onError(e: Throwable) {
                TODO("Not yet implemented")

            }

            override fun onComplete() {
                TODO("Not yet implemented")
            }

            override fun onNext(t: CardData) {
                Log.e(TAG,"onNext(CardData)")
                weatherCard.liveCardData.value = t
                Log.e(TAG," does onNext equal provider data ${t.equals(dataProviderUtil.cardData)}")

            }

        }
    }

    private fun getHourlyDataObserver(): Observer<WeatherData> {
        return object: Observer<WeatherData> {
            override fun onSubscribe(d: Disposable) {
                Log.w(TAG,"On subscribe to disposable")

            }

            override fun onError(e: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onComplete() {
                TODO("Not yet implemented")
            }

            override fun onNext(t: WeatherData) {
                Log.e(TAG,"onNext(WeatherData)")
                Log.e(TAG," does onNext equal provider data ${t.equals(dataProviderUtil.apiData.hourly)}")
                hourlyData.liveHourlyData.value = t.hourly

            }

        }
    }

    @Composable
    fun MainView(){
        var visable by remember {
            mutableStateOf(false)
        }

        var text by remember { mutableStateOf("") }

        val locationList = mutableStateListOf<Address>()

        val locations by remember {
            mutableStateOf(locationList)
        }


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
                AnimatedVisibility(visible = visable) {
                    Card(
                        shape = RoundedCornerShape(40.dp),

                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .fillMaxHeight(0.25f)

                        ,
                        backgroundColor = Color.White,

                    ){

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier
                                .background(Color.Transparent)


                        ) {
                            TextField(
                                value = text,
                                onValueChange = { text = it },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done,

                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        //call API
                                        val locationString = text.toString().lowercase().trim()

                                        val newLocation = Geocoder(applicationContext).getFromLocationName(locationString,5)
                                        if (newLocation != null) {
                                            locationList.clear()
                                            locationList.addAll(newLocation)
                                        }
                                        this.defaultKeyboardAction(imeAction = ImeAction.Done)

                                        Log.e(TAG,"calling api to search for this text: $text and got $newLocation")
                                    }
                                ),

                                maxLines = 1,
                                label = { Text("Enter new Location!") },
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                            )

                            LazyColumn(content = {
                                items(locationList.size){ item ->

                                    Log.e(TAG," lazy item scope = it ${locationList.get(item)}")
                                    TextButton(onClick = {
                                        val lat = locationList[item].latitude
                                        val long = locationList[item].longitude
                                        dataProviderUtil.updateLocation(lat,long)
                                    }) {
                                        Text(
                                            text =
                                            locationList[item].getAddressLine(0).toString(),
                                            color = Color.Black,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 18.sp
                                        )
                                        
                                    }
                                }
                            
                            })
                        }

                    }
                }


               weatherCard.WeatherCard(
                    callback = {
                        Log.e(TAG," callback!!!! $visable is not ${!visable}")
                        visable = !visable
                        text = ""
                    },
                    viewModel = weatherCard,
                    backgroundColor = LightBlue,

                )
                Spacer(modifier = Modifier.height(30.dp))
                hourlyData.HourlyDataRow(
                    viewModel = hourlyData
                )
            }

        }
    }


}

