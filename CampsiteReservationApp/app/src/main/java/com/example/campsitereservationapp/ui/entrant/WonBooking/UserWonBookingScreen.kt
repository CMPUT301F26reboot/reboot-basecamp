package com.example.campsitereservationapp.ui.entrant.WonBooking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import java.time.YearMonth
import java.util.Date

/**
 * UserWonBookingScreen.kt
 *
 * Lets them look at the campsite, read and accept the rules, pick a date on the calendar,
 * and either book or decline the offer.
 * Covers US 01.04.01 (view reservable dates) and US 01.04.03 (decline offer).
 *
 * Uses the Screen/Content pattern: [UserWonBookingScreen] is stateful and talks to
 * [UserWonBookingViewModel], while [UserWonBookingContent] is a plain stateless Composable that
 * only takes values and callbacks as parameters, so it's easy to read and test on its own.
 *
 * Outstanding issues:
 *  - Booking is not finished yet: it does not save a reservation (US 01.05.01).
 *
 * @param lotteryId the lottery the user won, passed through navigation.
 * @param campsiteName the campsite they chose (campsites are matched by name for now).
 * @param profileId the current user. Comes from device identification once that exists.
 * @param onBack called when the back button is tapped.
 * @param onFinished called after a successful decline so the caller can navigate away.
 * @param viewModel defaults to the real Compose [viewModel], can be swapped out for testing.
 */
@Composable
fun UserWonBookingScreen(
    lotteryId: String,
    campsiteName: String,
    profileId: String,
    onBack: () -> Unit,
    onFinished: () -> Unit,
    viewModel: UserWonBookingViewModel = viewModel()
) {
    LaunchedEffect(lotteryId, campsiteName) {
        viewModel.start(lotteryId, campsiteName)
    }

    val isFinished = viewModel.isFinished
    LaunchedEffect(isFinished) {
        if (isFinished) onFinished()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val errorMessage = viewModel.errorMessage
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
    }

    val campsite = viewModel.campsite

    UserWonBookingContent(
        isLoading = viewModel.isLoading,
        name = campsite?.name.orEmpty(),
        address = campsite?.address.orEmpty(),
        rating = campsite?.rating ?: 0.0,
        reviewNum = campsite?.numOfReviews ?: 0,
        maxOccupancy = campsite?.maxOccupancy ?: 0,
        ownerName = campsite?.ownerName.orEmpty(),
        description = campsite?.description.orEmpty(),
        tags = campsite?.tags.orEmpty(),
        photoUrls = campsite?.photoUrls.orEmpty(),
        rules = campsite?.rules.orEmpty(),
        rulesAccepted = viewModel.rulesAccepted,
        onRulesAcceptedChange = viewModel::onRulesAcceptedChanged,
        bookableDays = viewModel.bookableDays,
        selectedDays = viewModel.selectedDays,
        initialMonth = viewModel.initialMonth,
        noDatesAvailable = viewModel.noDatesAvailable,
        canBook = viewModel.canBook,
        isDeclining = viewModel.isDeclining,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onDayClick = viewModel::toggleDay,
        onBook = viewModel::onBookClicked,
        onDecline = { viewModel.declineOffer(profileId) }
    )
}

/**
 * The UI for this screen: campsite info, the rules & policies section, the availability
 * calendar, and the Book/Decline buttons with their confirmation pop-ups.
 */
@Composable
internal fun UserWonBookingContent(
    isLoading: Boolean,
    name: String,
    address: String,
    rating: Double,
    reviewNum: Int,
    maxOccupancy: Int,
    ownerName: String,
    description: String,
    tags: List<String>,
    photoUrls: List<String>,
    rules: String,
    rulesAccepted: Boolean,
    onRulesAcceptedChange: (Boolean) -> Unit,
    bookableDays: Set<Date>,
    selectedDays: Set<Date>,
    initialMonth: YearMonth?,
    noDatesAvailable: Boolean,
    canBook: Boolean,
    isDeclining: Boolean,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onDayClick: (Date) -> Unit,
    onBook: () -> Unit,
    onDecline: () -> Unit
) {
    var showBookDialog by rememberSaveable { mutableStateOf(false) }
    var showDeclineDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BookingActions(
                canBook = canBook,
                isDeclining = isDeclining,
                onBook = { showBookDialog = true },
                onDecline = { showDeclineDialog = true }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            FilledTonalIconButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = address,
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
                            text = "$rating ($reviewNum reviews)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    InfoRow(label = "Maximum Occupancy", value = "$maxOccupancy guests")
                    InfoRow(label = "Owner", value = ownerName)

                    SectionTitle("Description")
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (tags.isNotEmpty()) {
                        SectionTitle("Tags")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            tags.forEach { TagChip(it) }
                        }
                    }

                    if (photoUrls.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(photoUrls) { url ->
                                AsyncImage(
                                    model = decodeBase64Image(url),
                                    contentDescription = "Photo of $name",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
                    }

                    if (rules.isNotBlank()) {
                        RulesPoliciesSection(
                            rules = rules,
                            accepted = rulesAccepted,
                            onAcceptedChange = onRulesAcceptedChange
                        )
                    }

                    AvailabilityCalendar(
                        availableDays = bookableDays,
                        selectedDays = selectedDays,
                        onDayClick = onDayClick,
                        title = "Available spots",
                        initialMonth = initialMonth
                    )

                    if (noDatesAvailable) {
                        Text(
                            text = "No dates are available for this campsite right now.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }

    if (showBookDialog) {
        AlertDialog(
            onDismissRequest = { showBookDialog = false },
            title = { Text("Book $name?") },
            text = { Text("Confirm your reservation for the selected dates.") },
            confirmButton = {
                TextButton(onClick = {
                    showBookDialog = false
                    onBook()
                }) { Text("Book") }
            },
            dismissButton = {
                TextButton(onClick = { showBookDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Decline this offer?") },
            text = { Text("You will give up your chance to book $name.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeclineDialog = false
                    onDecline()
                }) { Text("Decline offer") }
            },
            dismissButton = {
                TextButton(onClick = { showDeclineDialog = false }) { Text("Keep offer") }
            }
        )
    }
}

private val ActionButtonShape = RoundedCornerShape(12.dp)
private val ActionButtonPadding = PaddingValues(vertical = 14.dp)

/** Pinned bottom bar with the Book and Decline Offer buttons. */
@Composable
private fun BookingActions(
    canBook: Boolean,
    isDeclining: Boolean,
    onBook: () -> Unit,
    onDecline: () -> Unit
) {
    Surface(tonalElevation = 3.dp) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBook,
                enabled = canBook,
                shape = ActionButtonShape,
                contentPadding = ActionButtonPadding,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Book", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = onDecline,
                enabled = !isDeclining,
                shape = ActionButtonShape,
                contentPadding = ActionButtonPadding,
                border = BorderStroke(1.dp, Color.Black),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Decline Offer", fontWeight = FontWeight.Bold)
            }
        }
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

/** Bold heading used above the Description and Tags sections. */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
    )
}

/**
 * Shows the campsite's rules as a bullet list, with a checkbox the entrant must check before
 * they can book. Covers US 02.11.01.
 */
@Composable
private fun RulesPoliciesSection(
    rules: String,
    accepted: Boolean,
    onAcceptedChange: (Boolean) -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle("Rules & Policies")
            Spacer(Modifier.height(8.dp))
            rules.lineSequence().filter { it.isNotBlank() }.forEach { line ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text("•  ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = line.trim(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAcceptedChange(!accepted) }
            ) {
                Checkbox(checked = accepted, onCheckedChange = onAcceptedChange)
                Text("Accept rules & policies", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Small rounded label for one campsite tag. */
@Composable
private fun TagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

