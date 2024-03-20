package com.ben.bensweatherapp.activity

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ben.bensweatherapp.data.WeatherData
import com.ben.bensweatherapp.data.presentation.CardData
import com.ben.bensweatherapp.viewmodel.HourlyDataRowViewModel
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import com.ben.bensweatherapp.viewmodel.MainViewModel
import com.ben.bensweatherapp.viewmodel.WeatherCardViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@AndroidEntryPoint
class MainActivity : ComponentActivity()
{

    private val viewModel by viewModels<MainViewModel>()
    private val hourlyDataVM by viewModels<HourlyDataRowViewModel>()
    private val cardDatVM by viewModels<WeatherCardViewModel>()

    val TAG = "MainActivity"
    var time: Long = 0L
    var lat: Double? = 0.0
    var long: Double? = 0.0
    var locationName:String = ""


    val DeepBlue =  Color(17,35,90)
    val LightBlue = Color(89,111,183)
    val Sage = Color(198,207,155)
    val Yellow = Color(246,236,169)
    val bgGradiant = Brush.verticalGradient(listOf(Sage,Yellow,LightBlue,DeepBlue).reversed())
    val cardGradiant = Brush.radialGradient(listOf(DeepBlue,LightBlue))

    init {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"onCreate called")

        val newLong = -122.083922
        val newLat = 37.4220936


        try{
            viewModel.dataProviderUtil.hourlyDataSubject.subscribe(getHourlyDataObserver())
            viewModel.dataProviderUtil.cardDataSubject.subscribe(getWeatherCardObserver())

        }catch (e:Exception){
            e.printStackTrace()
        }
        Log.d(TAG,"using data provider to force on next...")
        viewModel.dataProviderUtil.hourlyDataSubject.onNext(viewModel.dataProviderUtil.apiData)
        viewModel.dataProviderUtil.cardDataSubject.onNext(viewModel.dataProviderUtil.cardData)

        setContent {
            MainView()
        }
    }

    private fun getWeatherCardObserver(): Observer<CardData> {
        return object: Observer<CardData> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG,"On subscribe to disposable ")

            }

            override fun onError(e: Throwable) {
                TODO("Not yet implemented")

            }

            override fun onComplete() {
                TODO("Not yet implemented")
            }

            override fun onNext(t: CardData) {
                Log.d(TAG,"onNext(CardData)")
                viewModel.weatherCard.liveCardData.value = t
                viewModel.weatherCard.bitMapData.value = t.icon

            }

        }
    }

    private fun getHourlyDataObserver(): Observer<WeatherData> {
        return object: Observer<WeatherData> {
            override fun onSubscribe(d: Disposable) {
                Log.d(TAG,"On subscribe to disposable")

            }

            override fun onError(e: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onComplete() {
                TODO("Not yet implemented")
            }

            override fun onNext(t: WeatherData) {
                Log.d(TAG,"onNext(WeatherData)")
                viewModel.hourlyData.liveHourlyData.value = t.hourly

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
//                            val keyboardController = LocalSoftwareKeyboardController.current
                            val imme = LocalContext.current.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                                        if(locationString.isNullOrEmpty()){
                                            Log.d(TAG," no Location entered ")
                                        } else {
                                            val newLocation = Geocoder(applicationContext).getFromLocationName(locationString,10)
                                            if (newLocation != null) {
                                                locationList.clear()
                                                locationList.addAll(newLocation)
                                                Log.d(TAG,"calling api to search for this text: $text and got $newLocation")

                                            }
                                        }

                                        imme.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

                                    }
                                ),

                                maxLines = 1,
                                label = { Text("Enter new Location!") },
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                            )

                            LazyColumn(content = {
                                items(locationList.size){ item ->

                                    TextButton(onClick = {
                                        val lat = locationList[item].latitude
                                        val long = locationList[item].longitude
                                        viewModel.dataProviderUtil.updateLocation(lat,long)
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


                viewModel.weatherCard.WeatherCard(
                    callback = {
                        Log.d(TAG," callback!!!! $visable is not ${!visable}")
                        visable = !visable
                        text = ""
                    },
                    viewModel = viewModel.weatherCard,
                    backgroundColor = LightBlue,

                )
                Spacer(modifier = Modifier.height(30.dp))
                viewModel.hourlyData.HourlyDataRow(
                    viewModel = viewModel.hourlyData
                )
            }

        }
    }


}


