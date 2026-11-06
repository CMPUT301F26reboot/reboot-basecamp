package com.example.campsitereservationapp.ui.entrant.CampsiteSearch

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.components.AvailabilityCalendar
import java.time.YearMonth
import java.util.Date
import kotlin.collections.emptySet


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
    val scope = rememberCoroutineScope()
    var keyword by remember{mutableStateOf("")}
    var location by remember{mutableStateOf("")}
    var occupancyInput by remember{mutableStateOf("")}
    var occupancy by remember{mutableStateOf(0)}
    var selectedDates by remember{mutableStateOf(emptySet<Date>())}
    var searchResults by remember{mutableStateOf(emptyList<Listing>())}
    var showCalendar by remember{mutableStateOf(false)}
    var showCalText by remember{mutableStateOf("")}
    showCalText = "Date Selection"

    if(occupancyInput == "")
        occupancy = 0
    else
        occupancy = occupancyInput.toInt()

    Spacer(modifier = Modifier.padding(8.dp))

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)){
        FilledTonalIconButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth(fraction = 0.7f),
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text("Search Bar") },
            )

            Button(
                onClick = { vm.doSearch(keyword, location, occupancy) },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ){
                Text("Search")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            TextField(
                modifier = Modifier.fillMaxWidth(fraction = 0.5f),
                value = location,
                onValueChange = {location = it},
                label = {Text("Location")})

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = occupancyInput,
                onValueChange = {occupancyInput = it},
                label = {Text("Occupancy")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }


        Button(
            onClick = {
            showCalendar = !showCalendar
            if(showCalText == "Show Date Selection")
                showCalText = "Close Date Selection"
            else
                showCalText = "Show Date Selection"
                      },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 18.dp),
            border = BorderStroke(1.dp, Color.Black),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
        )
        {
            Text(showCalText)
        }

        if(showCalendar){
            AvailabilityCalendar(
                vm.get365Dates(), vm._selectedDates, vm::toggleDate,
                initialMonth = YearMonth.now())
        }
        LazyColumn(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(vm._searchResults){ listing ->
                UserSearchCampsiteCard(
                    listing = listing,
                    onSelect = {
                        onToDetails(listing.id, vm._selectedDates.minOrNull(), vm._selectedDates.maxOrNull())
                    }
                )
            }
        }
    }
}


/**
 * Composable card displaying details for a single campsite option within a search result.
 * Includes thumbnail image, location, capacity, star rating, availability dates, and a selection radio button.
 *
 * @param modifier optional [Modifier] applied to the outer [Surface].
 * @param isSelected whether this campsite card is currently selected by the user.
 * @param onSelect callback to details screen triggered when the user clicks this campsite card.
 */
@Composable
fun UserSearchCampsiteCard(
    listing: Listing,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onSelect: () -> Unit = {}
) {
    val cardBackground = if (isSelected) Color(0xFFF9FAFB) else Color.White
    val cardBorderColor = if (isSelected) Color(0xFF111827) else Color(0xFFE5E7EB)
    val cardBorderWidth = if (isSelected) 2.dp else 1.dp

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(16.dp),
        color = cardBackground,
        border = BorderStroke(cardBorderWidth, cardBorderColor),
        modifier = modifier
            .fillMaxWidth()
            .height(176.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image Box (occupies 25% width of row)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .height(95.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    //select first photo, show nothing if Null
                    val firstPhoto = listing.photoUrls.firstOrNull()
                    if (firstPhoto!= null) {
                        AsyncImage(
                            model = firstPhoto,
                            contentDescription = listing.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF6B7280)
                        )
                    }
                }

                // Information details about campsite (occupies rest of space)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = listing.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = listing.address,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color(0xFF6B7280)
                        )
                        Text(
                            text = "Up to ${listing.maxOccupancy} guests",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = Color(0xFFF59E0B)
                        )
                        Text(
                            text = "${listing.rating} (${listing.numOfReviews} reviews)",
                            fontSize = 11.sp,
                            color = Color(0xFF111827),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}


