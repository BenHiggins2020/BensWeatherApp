package com.ben.bensweatherapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
    LazyRow(
            Modifier
            .background(Color.Cyan)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,

        content = {
        items(2){
            miniHourlyCards()
        }
    })
}
@Composable
fun miniHourlyCards() {
    Box(
        modifier = Modifier
            .fillMaxHeight(.75f)
            .width(120.dp)
    ){
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()

        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.CenterHorizontally),
                backgroundColor = Color.White,
            ){
                Text(text = "Item Example",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally))
            }
        }
    }


}