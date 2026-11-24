/*
 * OwnerCampsiteDetailsScreen.kt
 *
 * Role: View layer for an owner inspecting one of their
 * campsites. It shows the campsite header and details, a calendar of reserved nights,
 * and a filterable list of the campsite's reservations. All data and filtering come
 * from OwnerReservationsViewModel. Navigation is left to the caller through
 * callbacks, so the screen holds no navigation logic.
 *
 * Outstanding issues:
 *  - Screen descriptions page shows that the reservations row should show
 *    the number of guests but no implementation to input number of guests for reservation
 *  - I have also noticed that the dates on the screen is 1 day later than it is shown
 *    in the Firestore, but I think that is due to us changing how we
 *    implement the dates and timezones
 */

package com.example.campsitereservationapp.ui.owner.CampsiteDetails

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.ui.components.AvailabilityCalendar
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ReservationFilter
import com.example.campsitereservationapp.ui.owner.CampsiteRegister.LabeledField
import com.example.campsitereservationapp.ui.owner.CampsiteRegister.SectionHeader
import com.example.campsitereservationapp.utils.decodeBase64Image
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Locale

/**
 * Owner details page for a single campsite.
 *
 * Shows "Listing not found" if [listingId] matches no loaded listing. Otherwise, it
 * lays out, top to bottom: header, details card, reservation calendar, filter buttons
 * (All / Active / Past / Cancelled) and the matching reservations.
 *
 * @param listingId id of the campsite to display.
 * @param onBack called when the back arrow is tapped.
 * @param onOpenReservation called with the reservation whose row was tapped.
 * @param viewModel source of listing, reservation and guest data.
 */

@Composable
fun OwnerCampsiteDetailsScreen(
    listingId: String,
    onBack: () -> Unit,
    onOpenReservation: (Reservation) -> Unit,
    viewModel: OwnerReservationsViewModel = viewModel()
) {
    val listing = viewModel.getListing(listingId)
    val reservations = viewModel.getFilteredReservations(listingId)
    val reservedDates = viewModel.getReservedDates(listingId)
    val bookedCount = viewModel.getBookedReservationCount(listingId)

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        "Campsite details",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    ) { padding ->
        if (listing == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Listing not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                //campsite name, image, location, and max occupancy
                item { CampsiteHeader(listing) }
                // campsite details
                item { CampsiteDetailsCard(listing) }
                // calendar
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "Reservation calendar",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "$bookedCount reservations · ${reservedDates.size} nights reserved this month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AvailabilityCalendar(
                            availableDays = reservedDates,
                            selectedDays = emptySet(),
                            onDayClick = {},
                            title = "Availability",
                            initialMonth = YearMonth.now(),
                            viewReservations = true
                        )
                    }
                }
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Reservations",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    "$bookedCount booked",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        // the filters for the reservations
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = viewModel.filter == ReservationFilter.All,
                                onClick = { viewModel.onFilterSelected(ReservationFilter.All) },
                                label = { Text("All") }
                            )
                            FilterChip(
                                selected = viewModel.filter == ReservationFilter.Reserved,
                                onClick = { viewModel.onFilterSelected(ReservationFilter.Reserved) },
                                label = { Text("Active") }
                            )
                            FilterChip(
                                selected = viewModel.filter == ReservationFilter.Past,
                                onClick = { viewModel.onFilterSelected(ReservationFilter.Past) },
                                label = { Text("Past") }
                            )
                            FilterChip(
                                selected = viewModel.filter == ReservationFilter.Cancelled,
                                onClick = { viewModel.onFilterSelected(ReservationFilter.Cancelled) },
                                label = { Text("Cancelled") }
                            )
                        }
                    }
                }

                if (reservations.isEmpty()) {
                    item {
                        Text(
                            "No reservations found for this filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    // the reservations to be clicked
                    item {
                        Column {
                            reservations.forEach { reservation ->
                                ReservationRow(
                                    reservation = reservation,
                                    guestName = viewModel.getGuestName(reservation.entrantID),
                                    onClick = { onOpenReservation(reservation) }
                                )
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}


/**
 * Top block of the screen: first photo, campsite name, address
 * and maximum occupancy.
 *
 * @param listing the campsite to summarise.
 */
@Composable
private fun CampsiteHeader(listing: Listing) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = decodeBase64Image(listing.photoUrls.firstOrNull()),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.width(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                listing.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                listing.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Up to ${listing.maxOccupancy} guests", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/**
 * Card that is listing the campsite's name, address, max occupancy, description and
 * coordinates.
 *
 * @param listing the campsite to describe.
 */
@Composable
private fun CampsiteDetailsCard(listing: Listing) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(
                title = "Campsite details",
                subtitle = ""
            )
            LabeledField(label = "Campsite name") {
                Text(listing.name, style = MaterialTheme.typography.bodyLarge)
            }
            LabeledField(label = "Address") {
                Text(listing.address, style = MaterialTheme.typography.bodyLarge)
            }
            LabeledField(label = "Max occupancy") {
                Text("${listing.maxOccupancy} Guests", style = MaterialTheme.typography.bodyLarge)
            }
            LabeledField(label = "Description") {
                Text(listing.description, style = MaterialTheme.typography.bodyLarge)
            }
            LabeledField(label = "Location") {
                Text(
                    "${listing.address} · ${listing.geoLocation.latitude}, ${listing.geoLocation.longitude}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

/**
 * One clickable row in the reservation list: start date tile, date range, guest name
 * and number of nights. A reservation with no dates shows "No dates".
 *
 * @param reservation the reservation to show.
 * @param guestName display name of the guest who booked it.
 * @param onClick called when the row is tapped.
 */
@Composable
private fun ReservationRow(
    reservation: Reservation,
    guestName: String,
    onClick: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM d", Locale.getDefault())
    val startDate = reservation.dates.firstOrNull()
    val endDate = reservation.dates.lastOrNull()
    val nights = reservation.dates.size

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(56.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(startDate?.let { SimpleDateFormat("MMM", Locale.getDefault()).format(it) } ?: "", style = MaterialTheme.typography.labelSmall)
                Text(startDate?.let { SimpleDateFormat("d", Locale.getDefault()).format(it) } ?: "", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (startDate != null && endDate != null) "${dateFormatter.format(startDate)}–${dateFormatter.format(endDate)}" else "No dates",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$guestName ",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "$nights nights",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}