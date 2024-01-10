package com.ben.bensweatherapp.util

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.ben.bensweatherapp.AppModule
import com.ben.bensweatherapp.data.Hourly
import com.ben.bensweatherapp.data.WeatherData
import com.ben.bensweatherapp.data.presentation.CardData
import com.ben.bensweatherapp.mappers.toCardData
import com.ben.bensweatherapp.mappers.toWeatherIcon
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BensDataProviderUtil(context:Context,activity: Activity) {

    var long = 0.0
    var lat = 0.0
    var time = 0L
    var locationName:String = ""
    lateinit var apiData: WeatherData
    lateinit var cardData: CardData

    val activity = activity
    val cardDataSubject:Subject<CardData> = PublishSubject.create()
    val hourlyDataSubject:Subject<WeatherData> = PublishSubject.create()

    val context = context
    val TAG = BensDataProviderUtil::class.java.simpleName

    init{

        getLocation().also {
            if(it.isCompleted){
                if(long != null && lat != null){
                    try {
                        val addresses = Geocoder(context).getFromLocation(lat, long, 5)
                        Log.d(TAG,"BEN address - ${addresses}")

                        locationName = addresses?.get(0)?.locality +", "+ addresses?.get(0)?.adminArea ?: ""
                        Log.d(TAG,"BEN address - ${addresses?.get(0)?.adminArea} $locationName ")
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }else if(it.isCancelled){
                Log.w(TAG,"getLocation Job is cancelled")
            }
        }

        callWeatherApi().also {
            if(it.isCompleted){
                Log.w(TAG," job is compete")
                callIconApi().also {
                    if(it.isCompleted) {
                        Log.w(TAG," callIconApi is complete")
                        hourlyDataSubject.onNext(apiData)
                        cardDataSubject.onNext(cardData)
                    }
                }
            } else if(it.isCancelled) {
                Log.w(TAG," job is cancelled")

            }
            while (it.isActive){
                Log.d(TAG,"api call is active")
            }

        }

    }

    fun updateLocation(lat:Double, long:Double){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
            Log.e(TAG,"getLocation - permission not granted... requesting permission")
        }

        this.lat = lat
        this.long = long
        val geocoder = Geocoder(context)
        val location = geocoder.getFromLocation(lat,long,3)
        locationName = location?.get(0)?.locality +", "+ location?.get(0)?.adminArea ?: ""

        callWeatherApi().also {
            if(it.isCompleted){
                Log.w(TAG," updateLocation finished, updating values!")
                hourlyDataSubject.onNext(apiData)
                cardDataSubject.onNext(cardData)
            }
        }
        Log.d(TAG,"location from geocoder = $location")

    }

    fun getWeatherCardSubject(): Observable<CardData> {
        return cardDataSubject.share()
    }

    fun getHourlyDataSubject(): Observable<WeatherData>{
        return hourlyDataSubject.share()
    }

    private suspend fun getPermissions() = runBlocking{
        launch {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
        }
    }
    fun getLocation() = runBlocking{
        launch {
            Log.e(TAG,"getLocation coroutine launched")
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Log.e(TAG,"getLocation - permission not granted... requesting permission")

                val job = launch {
                    getPermissions()
                }
                job.join()

            }
                val lm = context.getSystemService(Service.LOCATION_SERVICE) as LocationManager

                val hasGps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val hasNetwork  = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                val hasFused = lm.isProviderEnabled(LocationManager.FUSED_PROVIDER)

                if(hasNetwork){
                    lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        5000,
                        0F,
                        object: LocationListener {
                            override fun onLocationChanged(p0: Location) {
                            }

                        })
                }
                if(hasGps){
                    lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        5000,
                        0F,object: LocationListener {
                            override fun onLocationChanged(p0: Location) {
                            }

                        })


                }
                if(hasFused){
                    lm.requestLocationUpdates(
                        LocationManager.FUSED_PROVIDER,
                        5000,
                        0F,
                        object: LocationListener {
                            override fun onLocationChanged(p0: Location) {
                            }

                        })
                }

                val gpsLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                val networkLocation = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                val fusedLocation = lm.getLastKnownLocation(LocationManager.FUSED_PROVIDER)

                val networkAccuracy = networkLocation?.accuracy ?: 0.0F
                val fusedAccuracy = fusedLocation?.accuracy ?: 0.0F
                val gpsAccuracy = gpsLocation?.accuracy ?: 0.0F

                val order = arrayOf(networkAccuracy,fusedAccuracy,gpsAccuracy).also { it.sort() }
                Log.d(TAG,"order of array = ${order.get(2)}")

                when(order.get(2)){
                    gpsAccuracy ->{
                        Log.e(TAG,"highest accuracy = GPS")
                        val location = gpsLocation
                        if(location != null){
                            lat = location?.latitude!!
                            long = location?.longitude!!
                            if(location?.time !=null){
                                time = location?.time!!
                            }
                        }

                        else {
                            Log.e(TAG," time is null!");
                        }
                        Log.e(TAG,"Time = time $time")
                    }
                    fusedAccuracy -> {
                        Log.d(TAG,"highest accuracy = FUSED")
                        val location = fusedLocation
                        if(location != null){
                            lat = location.latitude
                            long = location.longitude
                            time = location.time

                        }
                        Log.e(TAG,"Time = time $time")
                    }
                    networkAccuracy -> {
                        Log.d(TAG,"highest accuracy = Network")
                        val location = networkLocation
                        if(location != null){
                            lat = location.latitude
                            long = location.longitude
                            time = location.time

                        }
                        Log.e(TAG,"Time = time $time")

                    }



            }


/*            if(gpsLocation != null){




                if( networkLocation != null && gpsLocation.accuracy < networkLocation.accuracy ){
                    val location = gpsLocation
                    lat = location?.latitude
                    long = location?.longitude
                    time = gpsLocation.getTime()
                    Log.e(TAG,"Time = time $time")
                }
                else if(networkLocation != null && gpsLocation.accuracy < networkLocation.accuracy){
                    val location = networkLocation
                    lat = location?.latitude
                    long = location?.longitude

                }


            }*/
            Log.e(TAG,"getLocation coroutine finished")

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
            Log.d(TAG,"Api call = ${apiData.daily.time.get(0)}")
            //TODO: call observers

        }
    }
    fun callIconApi() = runBlocking {
        launch {
            val res = AppModule.getIconApi().getIcon(cardData.weather_code.toWeatherIcon(cardData.weather_code))

            Log.e(TAG,"icon API call  ${res.contentType()}")
            val bitmap = BitmapFactory.decodeStream(res.byteStream())
            cardData.icon = bitmap
            //TODO: call observers



        }

    }
}