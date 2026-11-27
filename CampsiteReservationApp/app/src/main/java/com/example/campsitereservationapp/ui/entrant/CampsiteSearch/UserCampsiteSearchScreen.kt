/**
 * This file holds the User CampsiteSearchScreen and any other small helper UI
 */
package com.example.campsitereservationapp.ui.entrant.CampsiteSearch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.SearchBarComponent
import com.example.campsitereservationapp.ui.components.AvailabilityCalendar
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import com.example.campsitereservationapp.ui.components.DateRangeCalendarDialog
import com.example.campsitereservationapp.ui.components.InputDateScreenComponent
import com.example.campsitereservationapp.ui.components.formatDateRange
import com.example.campsitereservationapp.ui.components.nextDateRange
import java.time.YearMonth
import java.util.Date
import com.example.campsitereservationapp.ui.theme.RatingAmber


@Composable
        /**
         * Displays user campsite search screen
         *
         * Displays a search ui allowing the user to search for campsites by name, location, occupancy, and dates available
         *
         * @param [vm] is the associated viewmodel
         * @param [modifier] is optional modifier to the main Column
         * @param [onBack] is a lambda to return to previous screen
         * @param [onToDetails] is a lambda to go to campsite details screen with a provided listing ID
         * and first/last selected search dates (otherwise null if not selected)
         */
fun UserCampsiteSearchScreen(
    modifier: Modifier = Modifier,
    vm: UserCampsiteSearchViewModel = viewModel(),
    onBack: () -> Unit,
    onToDetails: (listingId: String, start: Date?, end: Date?) -> Unit
){
    var location by remember{mutableStateOf("")}
    var occupancyInput by remember{mutableStateOf("")}
    var occupancy by remember{mutableStateOf(0)}
    var showCalendar by remember{mutableStateOf(false)}
    val dates = vm._selectedDates.toList()



    if(occupancyInput == "") occupancy = 0
    else occupancy = occupancyInput.toInt()


    Column(modifier = modifier.padding(12.dp), verticalArrangement = Arrangement.Center){
        BackButtonAndHeaderScreenComponent(
            title = "Browse Campsites",
            onBack = onBack
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SearchBarComponent(
                onSearch = { keyword ->
                    vm.doSearch(keyword, location, occupancy)
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            TextField(
                modifier = Modifier.fillMaxWidth(fraction = 0.5f).padding(5.dp),
                value = location,
                onValueChange = {location = it},
                label = {Text("Location")},
                shape = MaterialTheme.shapes.medium,
            )

            TextField(
                modifier = Modifier.fillMaxWidth().padding(5.dp),
                value = occupancyInput,
                onValueChange = {occupancyInput = it},
                label = {Text("Occupancy")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = MaterialTheme.shapes.medium,
            )
        }

        //AI PROMPT COPILOT:
        //How to I make a Text field, but instead of asking for an input, when you click it,
        // it opens up the calendar and the text updates based on what the dates are chosen.
        // Also, the purpose of this is to match the UI of the Text fields above

        //Date Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable { showCalendar = true }
        ) {
            TextField(
                value = if (dates.isEmpty()) "" else formatDateRange(dates),
                onValueChange = { },
                readOnly = true,
                enabled = false,
                label = { Text("Dates") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Select Dates",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        //display campsites
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(vm._searchResults){ listing ->
                CampsiteCard(
                    listing = listing,
                    onDetailsClick = {
                        onToDetails(listing.id, vm._selectedDates.minOrNull(), vm._selectedDates.maxOrNull())
                    }
                )
            }
        }

        //Popup Calendar to select range of dates
        if(showCalendar){
            DateRangeCalendarDialog(
                dates = dates,
                onSelection = { tappedDay ->
                    // Calculates the full date range between first tap and second tap
                    val newRange = nextDateRange(vm._selectedDates.toList(), tappedDay)

                    // Update ViewModel selected dates
                    vm._selectedDates.clear()
                    vm._selectedDates.addAll(newRange)
                },
                onDismiss = { showCalendar = false },
                title = "Select Dates",
            )
        }
    }
}


