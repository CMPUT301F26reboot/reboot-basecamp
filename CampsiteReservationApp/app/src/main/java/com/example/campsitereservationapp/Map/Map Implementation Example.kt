package com.example.campsitereservationapp.Map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.GeoPoint
import java.util.Locale

/** Example/Demo on how to add to Screen:
 * Has 2 Parts:
 *  MapCompose: Shows map based on coordinate input, also has button to drop pin and get geolcation
 *  SomeTextField + DropDown Menu = search for address and move map to there
 *
 *
 */
@Composable
fun MapImplementationExample(
    geocodingViewModel: GeocodingViewModel = viewModel(),
    mapViewModel: MapViewModel = viewModel()
) {

    var query by remember { mutableStateOf("") } //User input string
    var expanded by remember { mutableStateOf(false) } //bool to show dropdown menu
    val suggestions by geocodingViewModel.suggestions.collectAsState() //suggested locations from query
    val coordinates by geocodingViewModel.coordinates.collectAsState() //geolcation coordinates returned from search
    val droppedPinCoordinates = mapViewModel.droppedPin //note: type  Pair<Double, Double>? = Pair<lat,lon>

    Column(modifier = Modifier.padding(16.dp)) {
        Box {
            //Where input address goes, updates expanded to show dropdown menu
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    expanded = true
                    geocodingViewModel.onQueryChange(it) //updates suggestions
                }
            )

            //Shown after adress input (may take a couple seconds, its very slow (limited to 1 query per second)
            DropdownMenu(
                expanded = expanded && suggestions.isNotEmpty(),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = false) // keeps the keyboard open
            ) {
                //drop down suggestions
                suggestions.forEach { address ->
                    DropdownMenuItem(
                        text = { Text(address) },
                        onClick = {
                            query = address
                            expanded = false
                            geocodingViewModel.onAddressSelected(address) // map updates here
                        }
                    )
                }
            }
        }

        MapScreenComponent(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            latitude = coordinates?.first ?: 53.5232,
            longitude = coordinates?.second ?: -113.5263,
            viewModel = mapViewModel
        )

        if (droppedPinCoordinates != null) {
            Text("GeoLocation(%.4f, %.4f)".format(droppedPinCoordinates.first, droppedPinCoordinates.second))

            //example of adding to database
            //listing.geoLocation = GeoPoint(droppedPinCoordinates.first,droppedPinCoordinates.second )
        }


    }


}

@Preview(showBackground = true)
@Composable
fun MapImplementationExamplePreview() {
    MapImplementationExample()
}