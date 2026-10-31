package com.example.campsitereservationapp.ui.entrant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ReservationFilter
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.UserReservationsViewModel
import java.time.LocalDate


/**
 * User Reservations screen - US 01.06.01 (view active), US 01.06.02 (view past + rate),
 * US 01.07.01 (cancel active).
 *
 * Outstanding issues:
 *  So far this is just a skeleton for the layout.
 *  Have not implemented reviews or cancelling a reservation
 */
@Composable
fun UserReservationsScreen(
    profileId: String,
    onBack: () -> Unit,
    onOpenReservation: (reservationId: String) -> Unit = {},
    viewModel: UserReservationsViewModel = viewModel()
){
    val rows = viewModel.filteredReservations(profileId)
        .map { it to viewModel.listingFor(it) }

    UserReservationContent(
        filter = viewModel.filter,
        rows = rows,
        isCancelling = viewModel.isCancelling,
        reviewingReservationId = viewModel.reviewingReservationId,
        isSubmittingReview = viewModel.isSubmittingReview,
        onFilterChange = viewModel::onFilterSelected,
        onBack = onBack,
        onOpen = onOpenReservation,
        onCancel = viewModel::cancelReservation,
        onOpenReview = viewModel::openReview,
        onCloseReview = viewModel::closeReview,
        onSubmitReview = { id, campsiteId, rating, text ->
            viewModel.submitReview(id, campsiteId, profileId, rating, text)
        }
    )
}

@Composable
fun UserReservationContent(
    filter: ReservationFilter,
    rows: List<Pair<Reservation, Listing?>>,
    isCancelling: Boolean,
    reviewingReservationId: String?,
    isSubmittingReview: Boolean,
    onFilterChange: (ReservationFilter) -> Unit,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onCancel: (String) -> Unit,
    onOpenReview: (String) -> Unit,
    onCloseReview: () -> Unit,
    onSubmitReview: (reservationId: String, campsiteId: String, rating: Int, text: String) -> Unit)
    {
        var pendingCancelId by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilledTonalIconButton(onClick = onBack, shape = CircleShape) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center


        ) {
            Button(
                onClick = { }, //need to navigate to lotteries screen
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    "Lotteries",
                    style = MaterialTheme.typography.titleMedium

                )
            }
            Spacer(Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { /* Already on this screen */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        "Reservations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                // the underline bar
                Box(
                    Modifier
                        .width(120.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        // filter buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //all button
            Button(
                onClick = { onFilterChange(ReservationFilter.All) },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text("All", fontWeight = FontWeight.Medium)
            }
            //reserved button
            Button(
                onClick = { onFilterChange(ReservationFilter.Reserved) }
            ) {
                Text("Reserved", fontWeight = FontWeight.Medium)
            }
            // past button
            Button(
                onClick = { onFilterChange(ReservationFilter.Past) }
            ) {
                Text("Past", fontWeight = FontWeight.Medium)
            }

        }
        Spacer(Modifier.height(16.dp))

        Text(
            "Your reservations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(Modifier.height(8.dp))
        //if no reservations
        if (rows.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (filter == ReservationFilter.Past)
                        "No past reservations." else "No active reservations.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rows, key = { it.first.id }) { (reservation, listing) ->
                    val past = isPast(reservation)
                    ReservationCard(
                        reservation = reservation,
                        listing = listing,
                        isPast = past,
                        isCancelling = isCancelling,
                        onOpen = { onOpen(reservation.id) },
                        onCancel = { pendingCancelId = reservation.id },
                        onLeaveReview = { onOpenReview(reservation.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReservationCard(
    reservation: Reservation,
    listing: Listing?,
    isPast: Boolean,
    isCancelling: Boolean,
    onOpen: () -> Unit,
    onCancel: () -> Unit,
    onLeaveReview: () -> Unit
) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Campsite Image
                Box(
                    Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    val url = listing?.photoUrls?.firstOrNull()
                    if (!url.isNullOrBlank()) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text("🏕️", style = MaterialTheme.typography.titleLarge)
                    }
                }
                Spacer(Modifier.width(12.dp))
                // campsite details
                Column(Modifier.weight(1f)) {
                    Text(
                        listing?.name ?: "Unknown campsite",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        listing?.address ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Star, contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            "${listing?.rating ?: 0.0}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "\uD83D\uDC64Max ${(listing?.maxOccupancy) ?: 0} guests",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))

                    Surface(
                        shape = RoundedCornerShape(50),
                    ) {
                        Text(
                            text = if (isPast) "Past reservation" else "Reserved",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (!isPast) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onOpen,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            )
                        ) { Text("View") }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {},//onCancel,
                            enabled = !isCancelling,
                        ) {
                            Text("Cancel Reservation", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {},//onLeaveReview,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text("Leave a review!", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

private fun isPast(r: Reservation): Boolean {
    val last = r.dates.maxOrNull() ?: return false
    return last.isBefore(LocalDate.now())
}




// ---------- previews ----------

private fun sampleListing(
    id: String, name: String, address: String, rating: Double
) = Listing(
     name = name, address = address, maxOccupancy = 4, rating = rating,
    description = "", ownerID = "o1", tags = emptyList(), photoUrls = emptyList()
).apply { this.id = id }


@Preview(showBackground = true, heightDp = 900)
@Composable
private fun UserReservationAllPreview() {
    val today = LocalDate.now()
    val active = Reservation(
        dates = listOf(today.plusDays(7), today.plusDays(8)),
        entrantID = "u1", campsiteID = "c1"
    ).apply { id = "r1" }
    val past = Reservation(
        dates = listOf(today.minusDays(30), today.minusDays(29)),
        entrantID = "u1", campsiteID = "c2"
    ).apply { id = "r2" } //this .apply{} is used assuming the id value in listing is a var not a val
    val pine = sampleListing("c1", "Pine Valley Retreat", "Yosemite, CA", 4.9)
    val red = sampleListing("c2", "Redwood Ridge", "Big Sur, CA", 4.8)
    MaterialTheme {
        UserReservationContent(
            filter = ReservationFilter.All,
            rows = listOf(active to pine, past to red),
            isCancelling = false,
            reviewingReservationId = null,
            isSubmittingReview = false,
            onFilterChange = {}, onBack = {}, onOpen = {}, onCancel = {},
            onOpenReview = {}, onCloseReview = {}, onSubmitReview = { _, _, _, _ -> }
        )
    }
}



@Preview(showBackground = true, heightDp = 700)
@Composable
private fun UserReservationEmptyPreview() {
    MaterialTheme {
        UserReservationContent(
            filter = ReservationFilter.All,
            rows = emptyList(),
            isCancelling = false,
            reviewingReservationId = null,
            isSubmittingReview = false,
            onFilterChange = {}, onBack = {}, onOpen = {}, onCancel = {},
            onOpenReview = {}, onCloseReview = {}, onSubmitReview = { _, _, _, _ -> }
        )
    }
}
