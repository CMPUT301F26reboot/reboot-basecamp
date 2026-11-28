/*
 * OwnerReservationDetailsScreen.kt
 *
 * Role: View layer for an owner inspecting one reservation on their campsite. It shows
 * the campsite, the guest and their contact details, and the stay dates, with buttons
 * to message the guest or reject the reservation. All data comes from
 * OwnerReservationsViewModel. (US 02.08.01, US 02.07.01, US 02.07.02, US 02.07.03)
 *
 * Outstanding issues:
 *  - Screen descriptions page shows a guest count, but there is no implementation to
 *    input the number of guests for a reservation
 *  - Rejecting a reservation doesn't notify the guest yet
 *  - Message guest has not been implemented yet
 */


package com.example.campsitereservationapp.ui.Owner.Reservations
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.owner.CampsiteDetails.OwnerReservationsViewModel
import com.example.campsitereservationapp.utils.appCalendar
import com.example.campsitereservationapp.utils.appDateFormat
import com.example.campsitereservationapp.utils.decodeBase64Image
import com.example.campsitereservationapp.utils.endOfDay
import com.example.campsitereservationapp.utils.toDayOnly
import java.util.Calendar
import java.util.Date

/**
 * Details of a single reservation, seen by the campsite owner.
 *
 * @param reservationId id of the reservation to show.
 * @param onBack called when the back arrow is tapped.
 * @param onMessageGuest called with the guest's profile id when "Message Guest" is tapped.
 * @param viewModel source of reservation, listing and guest data.
 */
@Composable
fun OwnerReservationDetailsScreen(
    reservationId: String,
    onBack: () -> Unit,
    onMessageGuest: (String) -> Unit,
    viewModel: OwnerReservationsViewModel = viewModel()
) {
    val reservation = viewModel.allReservations.firstOrNull { it.id == reservationId }
    val listing = reservation?.let { viewModel.listingFor(it) }
    var showRejectDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back")
                }
                Text(
                    "Reservation details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { padding ->
        if (reservation == null) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Reservation not found")
            }
            return@Scaffold
        }

        val guest = viewModel.getGuestProfile(reservation.entrantID)
        val guestName = viewModel.getGuestName(reservation.entrantID)
        val nights = reservation.dates.map { it.toDayOnly() }.distinct().sorted()
        val cancelled = reservation.status == "cancelled"
        val past = nights.lastOrNull()?.endOfDay()?.before(Date()) == true

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Campsite details
            if (listing != null) CampsiteSummary(listing)
            // guest name, photo, and reservation status
            GuestRow(
                name = guestName,
                photo = guest?.profilePicture.orEmpty(),
                status = when {
                    cancelled -> "Cancelled"
                    past -> "Past"
                    else -> "Reserved"
                }
            )
            // contact details
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Contact details", style = MaterialTheme.typography.labelMedium)
                    ContactLine(Icons.Filled.Email, "Email", guest?.email)
                    ContactLine(
                        Icons.Filled.Phone, "Phone", guest?.phoneNumber?.let
                        { formatPhone(it) })
                }
            }
            // reservation details
            StayDetails(nights)

            HorizontalDivider()

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Button(
                    onClick = { onMessageGuest(reservation.entrantID) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Message Guest")
                }
                // Only a current or upcoming reservation can be rejected
                if (!cancelled && !past) {
                    Button(
                        onClick = { showRejectDialog = true },
                        enabled = !viewModel.isCancelling,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reject Reservation")
                    }
                }
            }
        }
    }


    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Reject this reservation?") },
            text = { Text("The reservation will be cancelled. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.cancelReservation(reservationId)
                    showRejectDialog = false
                }) {
                    Text("Yes, reject", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) { Text("Keep it") }
            }
        )
    }
}

/**
 * Campsite photo, name and address.
 * @param listing the campsite to summarise
 */
@Composable
private fun CampsiteSummary(listing: Listing) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = decodeBase64Image(listing.photoUrls.firstOrNull()),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(listing.name, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
            Text(
                listing.address,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Guest picture (or initials if there isn't one), name and reservation status.
 *
 * @param name display name of the guest, also used for the initials if there is no photo
 * @param photo the guests profile picture
 * @param status the status of the reservation (reserved, cancelled, or past)
 */
@Composable
private fun GuestRow(name: String, photo: String, status: String) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
    val image = remember(photo) {
        if (photo.isBlank()) null else decodeBase64Image(photo)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (image != null) {
                AsyncImage(
                    model = image,
                    contentDescription = "Photo of $name",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(initials, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
            Text(
                status,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

/**
 * One labelled contact value with an icon. Shows "Not provided" if there is no value.
 *
 * @param icon icon drawn to the left of the text.
 * @param label heading for the value, e.g. "Email".
 * @param value the contact value, or null or blank if unknown.
 */

@Composable
private fun ContactLine(icon: ImageVector, label: String, value: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(
                if (value.isNullOrBlank()) "Not provided" else value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Check-in and check-out dates followed by a summary of the reserved nights.
 * Check-out is the day after the last night. Shows "No dates" if there are none.
 *
 * @param nights the reserved days, sorted from earliest to latest.
 */

@Composable
private fun StayDetails(nights: List<Date>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Stay details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        if (nights.isEmpty()) {
            Text("No dates")
            return@Column
        }

        val checkIn = nights.first()
        val checkOut = appCalendar().apply {
            time = nights.last()
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(Modifier.padding(16.dp)) {
                StayDate("Check-in", checkIn, Modifier.weight(1f))
                StayDate("Check-out", checkOut, Modifier.weight(1f))
            }
        }

        val format = appDateFormat("MMM d, yyyy")
        val range = if (nights.size == 1) {
            format.format(checkIn)
        } else {
            "${format.format(checkIn)} – ${format.format(nights.last())}"
        }
        Text(
            "${nights.size} ${if (nights.size == 1) "night" else "nights"} · Reserved nights: $range",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * A label with a full date and its weekday, in Alberta time.
 *
 * @param label heading for the date, e.g. "Check-in".
 * @param date the day to show.
 * @param modifier layout modifier applied to the column.
 */

@Composable
private fun StayDate(label: String, date: Date, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(appDateFormat("MMMM d, yyyy").format(date),
            style = MaterialTheme.typography.titleSmall , fontWeight = FontWeight.Bold)
        Text(appDateFormat("EEEE").format(date), style = MaterialTheme.typography.bodySmall)
    }
}

/**
 * Formats a North American phone number for the contact details card,
 * e.g. +1 (202) 555-0148.
 *
 * @param raw the phone number as the guest typed it.
 * @return the formatted number, or [raw] unchanged if it starts with a country code
 *   other than +1 or doesn't have 10 digits (after removing a leading 1).
 */
private fun formatPhone(raw: String): String {
    val trimmed = raw.trim()
    // for international numbers eg +44 123 456 7890
    if (trimmed.startsWith("+") && !trimmed.startsWith("+1")) return raw

    var digits = trimmed.filter { it.isDigit() } // turn string into numbers only
    if (digits.length == 11 && digits.startsWith("1")) digits = digits.drop(1)
    if (digits.length != 10) return raw

    return "+1 (${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6)}"
}
