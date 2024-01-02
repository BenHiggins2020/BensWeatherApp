package com.ben.bensweatherapp.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.ben.bensweatherapp.AppModule
import com.ben.bensweatherapp.data.WeatherData
import com.ben.bensweatherapp.data.presentation.CardData
import com.ben.bensweatherapp.mappers.toCardData
import com.ben.bensweatherapp.mappers.toWeatherIcon
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.util.*

class MainActivity : ComponentActivity() {
    val TAG = "WeatherAppMainActivity"

    lateinit var apiData:WeatherData
    lateinit var cardData: CardData


    var lat: Double? = 0.0
    var long: Double? = 0.0
    var locationName:String = ""

    val DeepBlue =  Color(17,35,90)
    val LightBlue = Color(89,111,183)
    val Sage = Color(198,207,155)
    val Yellow = Color(246,236,169)
    val bgGradiant = Brush.verticalGradient(listOf(Sage,Yellow,LightBlue,DeepBlue).reversed())
    val cardGradiant = Brush.radialGradient(listOf(DeepBlue,LightBlue))

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.w(TAG,"onCreate called")

        getLocation()
        if(long!= null && lat != null){
            try {
                // Get up to 5 addresses from the Geocoder
                val addresses = Geocoder(applicationContext).getFromLocation(lat!!, long!!, 5)
                locationName = addresses?.get(0)?.locality +", "+ addresses?.get(0)?.adminArea ?: ""

                Log.e(TAG,"BEN address - ${addresses?.get(0)?.adminArea} $locationName ")
                if(locationName != null && locationName.isNotEmpty()){
//                    cardData.locationName = locationName
                }
            } catch (e: IOException) {
                // Handle the exception
                e.printStackTrace()
            }
        }
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
                Log.d(TAG,"HOURLY DATA - ${apiData.hourly.time}")
                HourlyDataRow(apiData.hourly)

            }

        }
    }

    fun getLocation(){
        if(ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
            Log.e(TAG,"getLocation - permission not granted... requesting permission")
        }

        val lm = applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
        val providers = lm.getBestProvider(criteria,true)

        Log.e(TAG,"BEN - providers = $providers")

        val hasGps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork  = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val hasFused = lm.isProviderEnabled(LocationManager.FUSED_PROVIDER)
        if(hasNetwork){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                object:LocationListener{
                    override fun onLocationChanged(p0: Location) {
                        Log.e(TAG,"Network: onLocationChanged ${p0.longitude} ${p0.latitude}")
                    }

                })
        }
        if(hasGps){
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            5000,
            0F,object:LocationListener{
                    override fun onLocationChanged(p0: Location) {
                        Log.e(TAG,"GPS: onLocationChanged ${p0.longitude} ${p0.latitude}")
                    }

                })


        }
        if(hasFused){
            lm.requestLocationUpdates(LocationManager.FUSED_PROVIDER,
            5000,
            0F,
            object:LocationListener{
                override fun onLocationChanged(p0: Location) {
                    Log.e(TAG,"Fused: onLocationChanged ${p0.longitude} ${p0.latitude}")
                }

            })
        }

        val gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        val networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if(gpsLocation != null){
            if( networkLocation != null && gpsLocation.accuracy < networkLocation.accuracy ){
                val location = gpsLocation
                lat = location?.latitude
                long = location?.longitude
            }
            else if(networkLocation != null && gpsLocation.accuracy < networkLocation.accuracy){
                val location = networkLocation
                lat = location?.latitude
                long = location?.longitude



            }


        }



    }


    fun callWeatherApi() = runBlocking {
        launch {
            Log.e(TAG," API CALL latitude = $lat longitude = $long")
            apiData = AppModule.weatherApi().getWeatherData(latitude = lat.toString(), longitude = long.toString())
            cardData = apiData.daily.toCardData(0)
            if(locationName != null && locationName.isNotEmpty()){
                cardData.locationName = locationName
            }
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


