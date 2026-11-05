package com.example.campsitereservationapp.Map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ovh.plrapps.mapcompose.ui.MapUI


/** Map UI for other screens. Needs to be bounded in another component (or it will take up whole screen)
 * Credit:
 *  - Uses Open Source Map Compose: https://github.com/p-lr/MapCompose
 *  - and https://www.openstreetmap.org/#map=5/59.98/-116.32
 *  - mixture of CLAUDE and GEMINI was used to derive implementation
 * @param modifier The [Modifier] to be applied to this component.
 * @param latitude [double] The lattitude of GPS Location
 * @param longitude [double] The longitude of GPS Location
 * */
@Composable
fun MapScreenComponent(
    modifier: Modifier = Modifier,
    latitude: Double = 53.5232,
    longitude: Double = -113.5263,
    viewModel: MapViewModel = viewModel()
) {
    //move to given location first loaded in
    LaunchedEffect(latitude, longitude) {
        viewModel.moveTo(latitude, longitude)
    }

    Box(modifier = modifier) {
        MapUI(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { viewModel.zoomIn() },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            FloatingActionButton(
                onClick = { viewModel.zoomOut() },
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}