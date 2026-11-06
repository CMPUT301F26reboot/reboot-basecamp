package com.example.campsitereservationapp.ui.owner.campsitedetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.Map.GeocodingViewModel
import com.example.campsitereservationapp.Map.MapScreenComponent
import com.example.campsitereservationapp.Map.MapViewModel
import com.example.campsitereservationapp.ui.owner.CampsiteRegister.SectionHeader

private const val DEFAULT_LATITUDE = 51.1784
private const val DEFAULT_LONGITUDE = -115.5708

/**
 * Lets the owner search an address or drop a pin to set the campsite's location. Wraps the
 * shared map implementation in the Map/ package (MapScreenComponent, MapViewModel,
 * GeocodingViewModel).
 *
 * @param onLocationPicked called with (latitude, longitude) whenever the owner picks a point,
 * either by selecting a search result or dropping a pin on the map.
 */
@Composable
internal fun MapSection(onLocationPicked: (latitude: Double, longitude: Double) -> Unit) {
    val geocodingViewModel: GeocodingViewModel = viewModel()
    val mapViewModel: MapViewModel = viewModel()

    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val suggestions by geocodingViewModel.suggestions.collectAsState()
    val searchedCoordinates by geocodingViewModel.coordinates.collectAsState()
    val droppedPin = mapViewModel.droppedPin

    LaunchedEffect(searchedCoordinates) {
        searchedCoordinates?.let { (latitude, longitude) -> onLocationPicked(latitude, longitude) }
    }

    LaunchedEffect(droppedPin) {
        droppedPin?.let { (latitude, longitude) -> onLocationPicked(latitude, longitude) }
    }

    Column {
        SectionHeader(title = "Set Location on Map", subtitle = "Search an address or drop a pin to mark your campsite's exact location")
        Spacer(Modifier.height(12.dp))
        Box {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    expanded = true
                    geocodingViewModel.onQueryChange(it)
                },
                placeholder = { Text("Search address or place name...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            DropdownMenu(
                expanded = expanded && suggestions.isNotEmpty(),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = false)
            ) {
                suggestions.forEach { address ->
                    DropdownMenuItem(
                        text = { Text(address) },
                        onClick = {
                            query = address
                            expanded = false
                            geocodingViewModel.onAddressSelected(address)
                        }
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        MapScreenComponent(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            latitude = searchedCoordinates?.first ?: DEFAULT_LATITUDE,
            longitude = searchedCoordinates?.second ?: DEFAULT_LONGITUDE,
            viewModel = mapViewModel
        )
    }
}
