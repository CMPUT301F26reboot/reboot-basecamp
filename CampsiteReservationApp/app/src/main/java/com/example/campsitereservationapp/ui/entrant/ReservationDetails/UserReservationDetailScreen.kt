package com.example.campsitereservationapp.ui.entrant.ReservationDetails

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dangerous
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ReservationsViewModel
import com.example.campsitereservationapp.ui.components.AvailabilityCalendar
import com.example.campsitereservationapp.ui.theme.Emerald10
import com.example.campsitereservationapp.utils.decodeBase64Image
import com.example.campsitereservationapp.utils.toDayOnly
import com.example.campsitereservationapp.ui.theme.RatingAmber
import com.example.campsitereservationapp.ui.theme.PillShape
import com.example.campsitereservationapp.ui.theme.Red10
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

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
        if (viewModel.allReservations.isNotEmpty()) {
            LaunchedEffect(Unit) { onBack() }
            return
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
                tint = RatingAmber,
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
                            .clip(MaterialTheme.shapes.large)
                    )
                }
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    // reservation info
                    Icon(
                        imageVector = if(reservation.status == "reserved") {
                            Icons.Filled.CheckCircle
                        } else Icons.Filled.Dangerous,
                        contentDescription = "Checkmark",
                        tint = if(reservation.status == "reserved") {Emerald10} else Red10,
                        modifier = Modifier
                            .size(46.dp)
                            .padding(horizontal = 6.dp)
                    )
                    Column(Modifier
                        .weight(1f)
                        .padding(vertical = 3.dp)) {

                        Text(
                            text = if(reservation.status == "reserved") {
                                "Reservation Booked"
                            } else "Reservation Cancelled",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = if(reservation.status == "reserved") {
                                "Your stay at ${listing.name} has been confirmed.You will also receive a notification " +
                                        "for additional confirmation."
                            } else{
                                "Your stay at ${listing.name} has been cancelled.You will also receive a notification " +
                                        "for additional confirmation."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                        )

                        Spacer(Modifier.height(12.dp))

                        val context = LocalContext.current
                        Button(
                            onClick = {

                                // Below onClick function generated by Gemini 2026/11/25
                                // Q. "I want to print all the information in a reservation document to a csv in kotlin android studio.
                                // Just say we pass reservation as type Reservation where a databasemodel syncs firestore data of the
                                // reservation document into reservation of type Reservation. one of the fields is reservation.dates, which
                                // which is an array of timestamps, how can I format them to just yyyy-MM-dd so they are not too long in the
                                // csv file?

                                // Create csv File
                                val fileName = "reservation_${reservation.id.ifEmpty { "export" }}.csv"
                                val csvFile = File(context.cacheDir, fileName)

                                // Write the reservation to the csv
                                viewModel.exportToCsv(reservation, csvFile)

                                // share file using android share sheet
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    csvFile
                                )

                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/csv"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Export Reservation CSV"))
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                                contentColor = MaterialTheme.colorScheme.inverseSurface
                            ),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Row(){
                                Icon(
                                    imageVector = Icons.Filled.Download,
                                    contentDescription = "Download",
                                    modifier = Modifier
                                        .size(28.dp)
                                        .padding(horizontal = 6.dp)
                                )
                                Text("Download CSV", fontWeight = FontWeight.Bold)
                            }

                        }
                    }
                }
            }
        }
        AvailabilityCalendar(
            availableDays = emptySet(),
            selectedDays = reservation.dates.map { it.toDayOnly() }.toSet(),
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
        shape = PillShape,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
