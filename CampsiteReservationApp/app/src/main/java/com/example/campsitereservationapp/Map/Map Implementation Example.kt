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

@Composable
fun MapImplementationExample(
    geocodingViewModel: GeocodingViewModel = viewModel()
) {

    var query by remember { mutableStateOf("") } //User input string
    var expanded by remember { mutableStateOf(false) }
    val suggestions by geocodingViewModel.suggestions.collectAsState()
    val coordinates by geocodingViewModel.coordinates.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Box {
            OutlinedTextField(
                value = query,
                onValueChange = {
                    query = it
                    expanded = true
                    geocodingViewModel.onQueryChange(it)
                },
                label = { Text("Address") },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded && suggestions.isNotEmpty(),
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = false) // keeps the keyboard open
            ) {
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

        Text("© OpenStreetMap contributors")

        MapScreenComponent(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            latitude = coordinates?.first ?: 53.5232,
            longitude = coordinates?.second ?: -113.5263
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MapImplementationExamplePreview() {
    MapImplementationExample()
}