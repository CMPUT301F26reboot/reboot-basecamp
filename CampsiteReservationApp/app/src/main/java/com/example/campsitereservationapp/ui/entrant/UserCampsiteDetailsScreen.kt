package com.example.campsitereservationapp.ui.entrant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.ui.components.AvailabilityCalendar
import com.example.campsitereservationapp.ui.theme.CampsiteReservationAppTheme
import com.example.campsitereservationapp.ui.components.dateOf
import java.util.Date
import java.time.YearMonth
import kotlin.collections.plus
import com.example.campsitereservationapp.R

@Composable
fun UserCampsiteDetailsScreen(onBack: () -> Unit) {
    val march5 = dateOf(2027, 3, 5)
    val march6 = dateOf(2027, 3, 6)
    // Later: get the ViewModel here and pass its real data in
    UserCampsiteDetailsContent(
        isLoading = false,
        name = "Pine Valley Retreat",
        address = "1284 Pine Valley Road, Yosemite, CA 95389",
        rating = 4.8,
        reviewNum = 128,
        maxOccupancy = 4,
        ownerName = "Sarah J.",
        description = "Welcome to Pine Valley! Bring your family and friends and splash in " +
                "the river lake, hike through the forest, and enjoy the beautiful scenery!",
        tags = listOf("Fire Pit", "Swimming", "Hiking", "Picnic Table", "RVs"),
        photoUrls = listOf("https://picsum.photos/id/128/400/400"),
        availableLotteries = emptyList(), // Should be list of lotteries
        availableDays = setOf(march5, march6),
        initialMonth = YearMonth.of(2027, 3),
        noLotteriesAvailable = false,
        snackbarHostState = remember { SnackbarHostState() },
        onBack = onBack,
        onJoin = {}
    )
}

@Composable
internal fun UserCampsiteDetailsContent(
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
    availableLotteries: List<String>,
    availableDays: Set<Date>,
    initialMonth: YearMonth?,
    noLotteriesAvailable: Boolean,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onJoin: () -> Unit,
) {

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            /*
            BookingActions(
                canBook = canBook,
                isDeclining = isDeclining,
                onBook = { showBookDialog = true },
                onDecline = { showDeclineDialog = true }
            )
            */
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

                    Row() {

                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Orange Star",
                            tint = Color(0xFFFF9800), // Standard material orange
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = "$rating",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = "($reviewNum reviews)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                                    model = url,
                                    placeholder = painterResource(R.drawable.sample_campsite),
                                    error = painterResource(R.drawable.sample_campsite),
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
                        availableDays = availableDays,
                        selectedDays = emptySet(),
                        onDayClick = { },
                        title = "Available spots",
                        initialMonth = initialMonth
                    )

                    if (noLotteriesAvailable) {
                        Text(
                            text = "No lotteries are available for this campsite right now.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
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

@Preview(showBackground = true)
@Composable
private fun UserCampsiteDetailsPreview() {
    val march5 = dateOf(2027, 3, 5)
    val march6 = dateOf(2027, 3, 6)
    CampsiteReservationAppTheme {
        UserCampsiteDetailsContent(
            isLoading = false,
            name = "Pine Valley Retreat",
            address = "1284 Pine Valley Road, Yosemite, CA 95389",
            rating = 4.8,
            reviewNum = 128,
            maxOccupancy = 4,
            ownerName = "Sarah J.",
            description = "Welcome to Pine Valley! Bring your family and friends and splash in " +
                    "the river lake, hike through the forest, and enjoy the beautiful scenery!",
            tags = listOf("Fire Pit", "Swimming", "Hiking", "Picnic Table", "RVs"),
            photoUrls = listOf("https://picsum.photos/id/128/400/400"),
            availableLotteries = emptyList(), // Should be list of lotteries
            availableDays = setOf(march5, march6),
            initialMonth = YearMonth.of(2027, 3),
            noLotteriesAvailable = false,
            snackbarHostState = remember { SnackbarHostState() },
            onBack = {},
            onJoin = {}
        )
    }
}
