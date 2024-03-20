package com.ben.bensweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import com.ben.bensweatherapp.util.BensDataProviderUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val dataProviderUtil: BensDataProviderUtil,
    val weatherCard: WeatherCardViewModel,
    val hourlyData : HourlyDataRowViewModel,
) : ViewModel() {

}