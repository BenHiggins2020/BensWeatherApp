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
                Log.d(TAG,"getLocation Job is cancelled")
            }
        }

        callWeatherApi().also {
            if(it.isCompleted){
                Log.d(TAG," job is compete")
                callIconApi().also {
                    if(it.isCompleted) {
                        Log.d(TAG," callIconApi is complete")
                        hourlyDataSubject.onNext(apiData)
                        cardDataSubject.onNext(cardData)
                    }
                }
            } else if(it.isCancelled) {
                Log.d(TAG," job is cancelled")

            }
            while (it.isActive){
                Log.d(TAG,"api call is active")
            }

        }

    }

    fun updateLocation(lat:Double, long:Double){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0)
            Log.d(TAG,"getLocation - permission not granted... requesting permission")
        }

        this.lat = lat
        this.long = long
        val geocoder = Geocoder(context)
        val location = geocoder.getFromLocation(lat,long,3)
        var name1 = location?.get(0)?.locality
        if(name1 == "null" || name1.isNullOrEmpty()){
            Log.d(TAG,"BEN - Locality =  ${location?.get(0)?.locality} \n" +
                    "SubLocality =  ${location?.get(0)?.subLocality} \n" +
                    "SubAdminArea =  ${location?.get(0)?.subAdminArea} \n"+
                    "Locale =  ${location?.get(0)?.locale} \n"+
                    "thoroughfare =  ${location?.get(0)?.thoroughfare} \n"+
                    "premises =  ${location?.get(0)?.premises} \n"

            )
            name1 = location?.get(0)?.subAdminArea
        }
        locationName = name1 +", "+ location?.get(0)?.adminArea ?: ""

        callWeatherApi().also {
            if(it.isCompleted){
                callIconApi().also {
                    if(it.isCompleted){
                        cardDataSubject.onNext(cardData)
                    }
                }
                Log.d(TAG," updateLocation finished, updating values!")
                hourlyDataSubject.onNext(apiData)
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
            Log.d(TAG,"getLocation coroutine launched")
            if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Log.d(TAG,"getLocation - permission not granted... requesting permission")

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
                        Log.d(TAG,"highest accuracy = GPS")
                        val location = gpsLocation
                        if(location != null){
                            lat = location?.latitude!!
                            long = location?.longitude!!
                            if(location?.time !=null){
                                time = location?.time!!
                            }
                        }

                        else {
                            Log.d(TAG," time is null!");
                        }
                        Log.d(TAG,"Time = time $time")
                    }
                    fusedAccuracy -> {
                        Log.d(TAG,"highest accuracy = FUSED")
                        val location = fusedLocation
                        if(location != null){
                            lat = location.latitude
                            long = location.longitude
                            time = location.time

                        }
                        Log.d(TAG,"Time = time $time")
                    }
                    networkAccuracy -> {
                        Log.d(TAG,"highest accuracy = Network")
                        val location = networkLocation
                        if(location != null){
                            lat = location.latitude
                            long = location.longitude
                            time = location.time

                        }
                        Log.d(TAG,"Time = time $time")

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
            Log.d(TAG,"getLocation coroutine finished")

        }




    }
    fun callWeatherApi() = runBlocking {
        launch {
            Log.d(TAG," API CALL latitude = $lat longitude = $long")
            apiData = AppModule.weatherApi().getWeatherData(latitude = lat.toString(), longitude = long.toString())
            cardData = apiData.daily.toCardData(0)
            cardData.locationName = locationName
            Log.d(TAG,"callWeatherApi -> weather_code = ${cardData.weather_code}")
            Log.d(TAG,"Api call = ${apiData.daily.time.get(0)}")
            //TODO: call observers

        }
    }
    fun callIconApi() = runBlocking {
        launch {
            Log.d(TAG," callIconApi -> weather_code = ${cardData.weather_code}")
            val res = AppModule.getIconApi().getIcon(cardData.weather_code.toWeatherIcon(cardData.weather_code))

            Log.d(TAG,"icon API call  ${res.contentType()}")
            val bitmap = BitmapFactory.decodeStream(res.byteStream())
            cardData.icon = bitmap
            //TODO: call observers



        }

    }
}