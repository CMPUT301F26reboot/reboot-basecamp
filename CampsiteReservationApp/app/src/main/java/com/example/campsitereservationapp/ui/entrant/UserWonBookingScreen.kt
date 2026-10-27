package com.example.campsitereservationapp.ui.entrant

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.ui.components.AvailabilityCalendar
import java.time.LocalDate
import java.time.YearMonth

/**
 * Screen shown after an entrant wins a campsite lottery slot.
 * Covers US 01.04.01 (view reservable dates), US 01.04.03 (decline offer), and the
 * confirmation-popup UI for US 01.05.01 (book a reservation).
 *
 * this function is stateful and talks to
 * [UserWonBookingViewModel], while [UserWonBookingContent] is stateless UI that can be
 * exercised from the `@Preview` functions at the bottom without a database.
 *
 * Outstanding issues:
 *  - Confirming the Book dialog calls [UserWonBookingViewModel.onBookClicked], which does not
 *    yet create a Reservation (US 01.05.01 is not fully implemented).
 *  - No navigation , it can only be exercised through the
 *    previews below (or Android Studio's Interactive Preview mode).
 *
 * @param lotteryId the lottery the user won, passed through navigation.
 * @param campsiteName the campsite they chose (campsites are matched by name for now).
 * @param profileId the current user. Comes from device identification once that exists.
 * @param onBack called when the back button is tapped.
 * @param onFinished called after a successful decline so the caller can navigate away.
 * @param viewModel injected for testability; defaults to the real Compose [viewModel].
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
        maxOccupancy = campsite?.maxOccupancy ?: 0,
        ownerName = campsite?.ownerName.orEmpty(),
        description = campsite?.description.orEmpty(),
        tags = campsite?.tags.orEmpty(),
        photoUrls = campsite?.photoUrls.orEmpty(),
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
 * Stateless presentation layer for the won-booking screen: campsite summary, availability
 * calendar, and the Book/Decline actions with their confirmation dialogs.
 */
@Composable
internal fun UserWonBookingContent(
    isLoading: Boolean,
    name: String,
    address: String,
    maxOccupancy: Int,
    ownerName: String,
    description: String,
    tags: List<String>,
    photoUrls: List<String>,
    bookableDays: Set<LocalDate>,
    selectedDays: Set<LocalDate>,
    initialMonth: YearMonth?,
    noDatesAvailable: Boolean,
    canBook: Boolean,
    isDeclining: Boolean,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
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
                                    model = url,
                                    contentDescription = "Photo of $name",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                )
                            }
                        }
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

/** Rounded pill showing one campsite tag, e.g. "Fire Pit". */
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

/**
 * Preview: populated screen matching the won-booking mockup. Date selection, Book, and Decline
 * are all interactive here (via local [remember] state), so Android Studio's Interactive Preview
 * mode can exercise them without a ViewModel or database.
 */
@Preview(showBackground = true, heightDp = 900)
@Composable
private fun UserWonBookingPreview() {
    val march5 = LocalDate.of(2027, 3, 5)
    val march6 = LocalDate.of(2027, 3, 6)
    val bookable = setOf(march5, march6, LocalDate.of(2027, 3, 12), LocalDate.of(2027, 3, 13))
    var selected by remember { mutableStateOf(setOf(march5, march6)) }
    MaterialTheme {
        UserWonBookingContent(
            isLoading = false,
            name = "Pine Valley Retreat",
            address = "1284 Pine Valley Road, Yosemite, CA 95389",
            maxOccupancy = 4,
            ownerName = "Sarah J.",
            description = "Welcome to Pine Valley! Bring your family and friends and splash in " +
                    "the river lake, hike through the forest, and enjoy the beautiful scenery!",
            tags = listOf("Fire Pit", "Swimming", "Hiking", "Picnic Table", "RVs"),
            photoUrls = emptyList(),
            bookableDays = bookable,
            selectedDays = selected,
            initialMonth = YearMonth.of(2027, 3),
            noDatesAvailable = false,
            canBook = selected.isNotEmpty(),
            isDeclining = false,
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onDayClick = { day ->
                if (day in bookable) {
                    selected = if (day in selected) selected - day else selected + day
                }
            },
            onBook = {},
            onDecline = {}
        )
    }
}

/** Preview: the campsite has no bookable dates left in its lottery window. */
@Preview(showBackground = true, heightDp = 900)
@Composable
private fun UserWonBookingNoDatesPreview() {
    MaterialTheme {
        UserWonBookingContent(
            isLoading = false,
            name = "Pine Valley Retreat",
            address = "1284 Pine Valley Road, Yosemite, CA 95389",
            maxOccupancy = 4,
            ownerName = "Sarah J.",
            description = "Welcome to Pine Valley!",
            tags = listOf("Fire Pit"),
            photoUrls = emptyList(),
            bookableDays = emptySet(), selectedDays = emptySet(),
            initialMonth = YearMonth.of(2027, 3),
            noDatesAvailable = true, canBook = false, isDeclining = false,
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {}, onDayClick = {}, onBook = {}, onDecline = {}
        )
    }
}
