package com.example.mapmodel

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


/*
Example of how it can be integrated
 */
class OtherViewModel : ViewModel() {
    //Another Screen gets the latitude and longitude


    //Default values for the map (University of Alberta)
    var latitude by mutableStateOf(53.5232)
        private set
    var longitude by mutableStateOf(-113.5263)
        private set

    fun updateCoordinates(newLat: Double, newLng: Double) {
        latitude = newLat
        longitude = newLng
    }
}

@Composable
fun OtherScreen(
    modifier: Modifier = Modifier,
    viewModel: OtherViewModel = viewModel()
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp)
            )
    ) { //insert the screen in another composable
        MapScreen(
            latitude = viewModel.latitude,
            longitude = viewModel.longitude,
            modifier = Modifier.fillMaxSize()
        )
    }
}
