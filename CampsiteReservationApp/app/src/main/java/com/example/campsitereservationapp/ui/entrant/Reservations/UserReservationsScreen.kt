package com.example.campsitereservationapp.ui.entrant.Reservations

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
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.ui.entrant.ReservationDetails.ReservationFilter
import java.util.Date

private val buttonRed = Color(0xFFfef2f2)
private val textRed = Color(0xFFb91c1c)
private val outlineRed = Color(0xFFfecaca)
private val buttonGreen = Color(0xFFcff7d3)
private val textGreen = Color(0xFF166534)
private val buttonGrey = Color(0xFFEAEAEA)

/**
 * User Reservations screen — US 01.06.01 (view active), US 01.06.02 (view past + rate),
 * US 01.07.01 (cancel active).
 *
 * The entrant's Reservations screen.
 *
 * Split into two parts: [UserReservationsScreen] is the version that talks to
 * the ViewModel, and [UserReservationContent] is the UI version that just takes
 * values and callbacks.
 *
 * @param profileId the current user's Firestore document ID. Used to filter reservations by `entrantID`.
 * @param onBack called when the back button is tapped.
 * @param onOpenReservation called with a reservation id when the user taps "View".
 * @param viewModel defaults to the real Compose [viewModel], can be swapped out for testing.
*/

@Composable
fun UserReservationsScreen(
    profileId: String,
    onBack: () -> Unit,
    onOpenReservation: (reservationId: String) -> Unit = {},
    onToLotteries: () -> Unit,
    viewModel: UserReservationsViewModel = viewModel()
){
    val rows = viewModel.filteredReservations(profileId)
        .map { it to viewModel.listingFor(it) }

    UserReservationContent(
        filter = viewModel.filter,
        rows = rows,
        isCancelling = viewModel.isCancelling,
        pendingCancelId = viewModel.pendingCancelId,
        reviewingReservationId = viewModel.reviewingReservationId,
        reviewRating = viewModel.reviewRating,
        onFilterChange = viewModel::onFilterSelected,
        onBack = onBack,
        onOpen = onOpenReservation,
        onCancel = viewModel::onCancelClicked, //  Opens dialog
        onDismissCancel = viewModel::dismissCancelDialog, // Closes dialog
        onConfirmCancel = viewModel::confirmCancel,
        onLeaveReview = viewModel::onLeaveReviewClicked,
        onDismissReview = viewModel::dismissReviewSheet,
        onRatingChanged = viewModel::onRatingChanged,
        onSubmitReview = viewModel::submitReview,
        onLotteriesTab = onToLotteries


    )
}

/**
 * Stateless version of the screen. Everything it shows comes from parameters, and every
 * tap goes back out through a callback.
 *
 * @param rows the reservations, already paired with their Listing. Listing can be null if
 *   the listing was deleted.
 * @param pendingCancelId which reservation we're asking the user to confirm cancelling,
 *   or null if the dialog is closed.
 * @param reviewingReservationId which reservation the review dialog is for, or null if
 *   the dialog is closed.
 * @param reviewRating how many stars are currently selected (0 means nothing picked).
*/
@Composable
fun UserReservationContent(
    filter: ReservationFilter,
    rows: List<Pair<Reservation, Listing?>>,
    isCancelling: Boolean,
    pendingCancelId: String?,
    reviewingReservationId: String?,
    reviewRating: Int,
    onFilterChange: (ReservationFilter) -> Unit,
    onBack: () -> Unit,
    onOpen: (String) -> Unit,
    onCancel: (String) -> Unit,
    onDismissCancel: () -> Unit,
    onConfirmCancel: () -> Unit,
    onLeaveReview: (String) -> Unit,
    onDismissReview: () -> Unit,
    onRatingChanged: (Int) -> Unit,
    onSubmitReview: (Int) -> Unit,
    onLotteriesTab: () -> Unit
    ) {
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
                onClick = onLotteriesTab,
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                colors = if (filter == ReservationFilter.All) {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = buttonGrey,
                        contentColor = Color.Gray
                    )
                }
            ) {
                Text(
                    "All",
                    fontWeight = FontWeight.Medium,
                )
            }
            //reserved button
            Button(
                onClick = { onFilterChange(ReservationFilter.Reserved) },
                colors = if (filter == ReservationFilter.Reserved) {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = buttonGrey,
                        contentColor = Color.Gray
                    )
                }

            ) {
                Text("Reserved", fontWeight = FontWeight.Medium)
            }
            // past button
            Button(
                onClick = { onFilterChange(ReservationFilter.Past) },
                colors = if (filter == ReservationFilter.Past) {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = buttonGrey,
                        contentColor = Color.Gray
                    )
                }
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
                        onCancel = { onCancel(reservation.id) },
                        onLeaveReview = { onLeaveReview(reservation.id) }
                    )
                }
            }
        }
    }
    // --- The Cancel Confirmation Dialog ---
    if (pendingCancelId != null) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Cancel this reservation?") },
            text = { Text("Your dates will become available to other campers. This can't be undone.") },
            confirmButton = {
                TextButton(onClick = onConfirmCancel) {
                    Text("Yes, cancel", color = textRed)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissCancel) {
                    Text("Keep it")
                }
            }
        )
    }
    // --- review sheet thing ---
    if (reviewingReservationId != null) {
        Dialog(
            onDismissRequest = onDismissReview
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //.background(Color.Black.copy(alpha = 0.5f)) // Dim background
                    .clickable { onDismissReview() }, // Click outside to close
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = false) {}, // Prevent clicks passing through
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = Color.White
                ) {
                    val reservation = rows.firstOrNull { it.first.id == reviewingReservationId }
                    val campsiteName = reservation?.second?.name ?: "this campsite"

                    ReviewSheetContent(
                        campsiteName = campsiteName,
                        rating = reviewRating,
                        onRatingChange = onRatingChanged,
                        onSubmit = onSubmitReview
                    )
                }
            }
        }
    }
}


/**
 * One reservation card: image, campsite name, rating, occupancy, status, and the
 * action buttons for whichever state it's in. (past or reserved)
 */
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
        color = if(isPast){ Color.LightGray} else{Color.White},
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
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
                        color = if (isPast) buttonGrey else buttonGreen
                    ) {
                        Text(
                            text = if (isPast) "Past reservation" else "Reserved",
                            color = if (isPast) Color.Gray else textGreen,
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
                            onClick = onCancel,
                            enabled = !isCancelling,
                            border = BorderStroke(2.dp, outlineRed),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = buttonRed // Changes inside color
                            )


                        ) {
                            Text("Cancel Reservation",
                                fontWeight = FontWeight.Bold,
                                color = textRed)
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if(!reservation.rated) {
                        Button(
                            onClick = onLeaveReview,
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
}

/** True if the reservation's last booked date is before today. */
private fun isPast(r: Reservation): Boolean {
    val last = r.dates.maxOrNull() ?: return false
    return last.before(Date())
}

/**
 * The rating dialog body: a 1–5 star picker and a Submit button.
 * Doesn't own the rating itself — it reads it from [rating] and reports taps up.
 */
@Composable
private fun ReviewSheetContent(
    campsiteName: String,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    onSubmit: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Rate your experience",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))

        // Star Picker
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Rate $i",
                    tint = if (i <= rating) Color.Black else Color.Gray,
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onRatingChange(i) }
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (rating == 0) "Tap a star to rate" else "$rating out of 5 stars",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        Spacer(Modifier.height(32.dp))
        // submit button
        Button(
            onClick = { onSubmit(rating) },
            enabled = rating > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Submit", fontWeight = FontWeight.Bold)
        }
    }
}