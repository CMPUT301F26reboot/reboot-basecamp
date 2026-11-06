package com.example.campsitereservationapp.ui.entrant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.ui.components.AvailabilityCalendar
import com.example.campsitereservationapp.utils.decodeBase64Image

/**
 * Detail screen for a single reservation.
 *
 * A read-only version of the won-booking screen. Same layout (name, address,
 * rating, occupancy, owner, description, tags, photos), but the calendar is display-only
 * and there are no Book / Decline buttons.
 *
 * The booked dates are highlighted, everything else in the calendar is grey.
 *
 * Outstanding issues:
 *
 *  - The calendar opens on the current month. If the reservation is from a past month, the
 *    user has to navigate back to see their dates.
 *
 * @param reservationId the Firestore document id of the reservation to show.
 * @param onBack called when the back button is tapped.
 */

@Composable
fun ReservationDetailScreen(
    reservationId: String,
    onBack: () -> Unit,
    viewModel: ReservationsViewModel = viewModel()
) {
    // Find the reservation and listing
    val reservation = viewModel.allReservations.firstOrNull { it.id == reservationId }
    val listing = reservation?.let { viewModel.listingFor(it) }
    if (reservation == null || listing == null) {
        LaunchedEffect(Unit) {
            onBack()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilledTonalIconButton(
            onClick = onBack,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = listing.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = listing.address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "${listing.rating} (${listing.numOfReviews} reviews)",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        InfoRow(label = "Maximum Occupancy", value = "${listing.maxOccupancy} guests")
        InfoRow(label = "Owner", value = listing.ownerName)

        Text(
            text = "Description",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = listing.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (listing.tags.isNotEmpty()) {
            Text(
                text = "Tags",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listing.tags.forEach { tag -> TagChip(tag) }
            }
        }

        if (listing.photoUrls.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(listing.photoUrls) { url ->
                    AsyncImage(
                        model = if (url.startsWith("http")) url else decodeBase64Image(url),
                        contentDescription = "Photo of ${listing.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }
        }

        AvailabilityCalendar(
            availableDays = emptySet(),
            selectedDays = reservation.dates.toSet(),
            onDayClick = {},
            title = "Your Booked Dates",
            initialMonth = null
        )

        Spacer(Modifier.height(16.dp))
    }
}

/** One label/value row, e.g. "Maximum Occupancy" / "4 guests". */
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
/** Small rounded pill for one tag, e.g. "Fire Pit". */
@Composable
private fun TagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
