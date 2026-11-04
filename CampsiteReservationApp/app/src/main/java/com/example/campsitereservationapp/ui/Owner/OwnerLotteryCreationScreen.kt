package com.example.campsitereservationapp.ui.Owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.components.AvailabilityCalendar
import com.example.campsitereservationapp.ui.entrant.toDayOnly
import java.text.SimpleDateFormat
import java.time.YearMonth
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun OwnerLotteryCreationScreen(
    ownerID: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onCreate: () -> Unit = {},
    viewModel: OwnerLotteryCreationViewModel = viewModel()
) {
    var lotteryStart by rememberSaveable { mutableStateOf<Date?>(null) }
    var lotteryEnd by rememberSaveable { mutableStateOf<Date?>(null) }
    var selectedCampsiteIDs by rememberSaveable { mutableStateOf(listOf<String>()) }
    var availability by rememberSaveable { mutableStateOf(mapOf<String, Set<Date>>()) }
    var winnersToDraw by rememberSaveable { mutableIntStateOf(0) }
    var waitlistCap by rememberSaveable { mutableIntStateOf(0) }
    var showCreatedPopup by rememberSaveable { mutableStateOf(false) }

    val ownerListings = viewModel.getOwnerListings(ownerID)
    val selectedCampsites = ownerListings.filter { it.id in selectedCampsiteIDs }

    val start = lotteryStart
    val end = lotteryEnd

    // AvailabilityCalendar only lets you tap days in this set.
    val bookableDays = remember(end) {
        val firstDay = if (end != null) addDays(end, 1) else Date()
        datesBetween(firstDay, addDays(firstDay, 365)).toSet()
    }

    val canCreate = start != null &&
            end != null &&
            !end.before(start) &&
            selectedCampsites.isNotEmpty() &&
            selectedCampsites.all { (availability[it.id]?.size ?: 0) >= 2 } &&
            winnersToDraw > 0 &&
            waitlistCap >= winnersToDraw

    Scaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BackButtonTitleAndHeader(onBack = onBack)

            LotteryDatesComponent(
                start = lotteryStart,
                end = lotteryEnd,
                onStartChange = { lotteryStart = it },
                onEndChange = { newEnd ->
                    lotteryEnd = newEnd
                    // Drop picked days that are no longer after the lottery ends.
                    availability = availability.mapValues { (_, days) ->
                        days.filter { it.after(newEnd) }.toSet()
                    }
                }
            )

            CampsiteDropDownComponent(
                campsites = ownerListings,
                selectedCampsites = selectedCampsites,
                onSelect = { campsite ->
                    if (campsite.id !in selectedCampsiteIDs) {
                        selectedCampsiteIDs = selectedCampsiteIDs + campsite.id
                    }
                },
                onRemove = { campsite ->
                    selectedCampsiteIDs = selectedCampsiteIDs - campsite.id
                    availability = availability - campsite.id
                }
            )

            SetCampsiteAvailabilityComponent(
                selectedCampsites = selectedCampsites,
                bookableDays = bookableDays,
                availability = availability,
                onAvailabilityChange = { campsiteID, days ->
                    availability = availability + (campsiteID to days)
                }
            )

            LotteryQuotasComponent(
                winnersToDraw = winnersToDraw,
                waitlistCap = waitlistCap,
                onWinnersChange = { winnersToDraw = it },
                onWaitlistChange = { waitlistCap = it }
            )

            Button(
                onClick = {
                    if (start != null && end != null) {
                        viewModel.createAndAddLottery(
                            start = start,
                            end = end,
                            availability = availability,
                            winnersToDraw = winnersToDraw,
                            waitlistCap = waitlistCap,
                            onSuccess = { showCreatedPopup = true }
                        )
                    }
                },
                enabled = canCreate,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Create", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showCreatedPopup && end != null) {
        PopUpComponent(
            campsiteNames = selectedCampsites.map { it.name },
            lotteryEnd = end,
            onDone = {
                showCreatedPopup = false
                onCreate()
            }
        )
    }
}

@Composable
fun BackButtonTitleAndHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedIconButton(
            onClick = onBack,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Create Lottery",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Choose your campsites and lottery dates.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun LotteryDatesComponent(
    start: Date?,
    end: Date?,
    onStartChange: (Date) -> Unit,
    onEndChange: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    val startOptions = remember(end) {
        datesBetween(Date(), end ?: addDays(Date(), MAX_DAYS_AHEAD)).toSet()
    }
    val endOptions = remember(start) {
        datesBetween(start ?: Date(), addDays(Date(), MAX_DAYS_AHEAD)).toSet()
    }

    SectionCard(
        title = "Lottery dates",
        subtitle = "Set when the lottery starts and ends.",
        modifier = modifier
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LotteryDateField(
                label = "Lottery start date",
                date = start,
                selectableDays = startOptions,
                onDateChange = onStartChange,
                modifier = Modifier.weight(1f)
            )
            LotteryDateField(
                label = "Lottery end date",
                date = end,
                selectableDays = endOptions,
                onDateChange = onEndChange,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "These dates apply to all selected campsites.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** A label above a box that shows the date and opens a calendar when tapped. */
@Composable
private fun LotteryDateField(
    label: String,
    date: Date?,
    selectableDays: Set<Date>,
    onDateChange: (Date) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCalendar by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Surface(
            onClick = { showCalendar = true },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(52.dp)
                    .padding(horizontal = 12.dp)
            ) {
                if (date != null) {
                    Text(
                        text = formatDate(date),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Choose $label",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showCalendar) {
        Dialog(onDismissRequest = { showCalendar = false }) {
            AvailabilityCalendar(
                availableDays = selectableDays,
                selectedDays = setOfNotNull(date),
                onDayClick = { day ->
                    onDateChange(day)
                    showCalendar = false
                },
                title = label,
                initialMonth = (date ?: selectableDays.minOrNull())?.toYearMonth()
            )
        }
    }
}

@Composable
fun CampsiteDropDownComponent(
    campsites: List<Listing>,
    selectedCampsites: List<Listing>,
    onSelect: (Listing) -> Unit,
    onRemove: (Listing) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val options = campsites.filter { campsite -> campsite !in selectedCampsites }

    SectionCard(
        title = "Campsites",
        subtitle = "Select one or more of your campsites.",
        modifier = modifier
    ) {
        Surface(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .height(56.dp)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Choose Campsite",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowUp
                    else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (expanded) "Hide campsites" else "Show campsites"
                )
            }
        }

        if (expanded) {
            Spacer(Modifier.height(4.dp))
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                if (options.isEmpty()) {
                    Text(
                        text = "No campsites found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                options.forEach { campsite ->
                    CampsiteOption(
                        campsite = campsite,
                        onClick = {
                            onSelect(campsite)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (selectedCampsites.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Selected campsites",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (selectedCampsites.size >= 2) {
                    SelectedCountBadge(count = selectedCampsites.size)
                }
            }
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                selectedCampsites.forEach { campsite ->
                    SelectedCampsiteCard(
                        campsite = campsite,
                        onRemove = { onRemove(campsite) }
                    )
                }
            }
        }
    }
}

/** One row in the open dropdown: campsite name above its address. */
@Composable
private fun CampsiteOption(
    campsite: Listing,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = campsite.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = campsite.address,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Green pill that shows how many campsites are selected, e.g. "2 selected". */
@Composable
private fun SelectedCountBadge(count: Int) {
    val green = Color(0xFF1B5E20)
    Surface(
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = "$count selected",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = green,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/** A selected campsite: photo, name, address, guest limit and a remove button. */
@Composable
private fun SelectedCampsiteCard(
    campsite: Listing,
    onRemove: () -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = campsite.photoUrls.firstOrNull(),
                contentDescription = "Photo of ${campsite.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(12.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = campsite.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                CampsiteDetail(
                    icon = Icons.Outlined.LocationOn,
                    text = campsite.address
                )
                CampsiteDetail(
                    icon = Icons.Outlined.Person,
                    text = "Up to ${campsite.maxOccupancy} guests"
                )
            }
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Remove ${campsite.name}",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/** A small icon next to a line of campsite detail text. */
@Composable
private fun CampsiteDetail(
    icon: ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SetCampsiteAvailabilityComponent(
    selectedCampsites: List<Listing>,
    bookableDays: Set<Date>,
    availability: Map<String, Set<Date>>,
    onAvailabilityChange: (String, Set<Date>) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Availability of each campsite",
        subtitle = null,
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            selectedCampsites.forEach { campsite ->
                CampsiteAvailabilityRow(
                    campsiteName = campsite.name,
                    bookableDays = bookableDays,
                    days = availability[campsite.id].orEmpty(),
                    onDaysChange = { days -> onAvailabilityChange(campsite.id, days) }
                )
            }
        }
    }
}

/** One campsite's booking dates, with a calendar dialog to change them. */
@Composable
private fun CampsiteAvailabilityRow(
    campsiteName: String,
    bookableDays: Set<Date>,
    days: Set<Date>,
    onDaysChange: (Set<Date>) -> Unit
) {
    var showCalendar by rememberSaveable { mutableStateOf(false) }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Booking · $campsiteName",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (days.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = nightsLabel(days),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Surface(
                onClick = { showCalendar = true },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = formatDayRange(days),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedIconButton(
                        onClick = { showCalendar = true },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Change dates for $campsiteName",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    if (showCalendar) {
        Dialog(onDismissRequest = { showCalendar = false }) {
            Column {
                AvailabilityCalendar(
                    availableDays = bookableDays,
                    selectedDays = days,
                    onDayClick = { day -> onDaysChange(nextRange(days, day)) },
                    title = campsiteName,
                    initialMonth = (days.minOrNull() ?: bookableDays.minOrNull())?.toYearMonth(),
                    showLegend = true
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { showCalendar = false },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LotteryQuotasComponent(
    winnersToDraw: Int,
    waitlistCap: Int,
    onWinnersChange: (Int) -> Unit,
    onWaitlistChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SectionCard(
        title = "Lottery quotas",
        subtitle = "Set the number of winners and the waitlist capacity.",
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            QuotaRow(
                label = "Winners to Draw",
                description = null,
                value = winnersToDraw,
                onValueChange = onWinnersChange
            )
            QuotaRow(
                label = "Waitlist Capacity Cap",
                description = "Maximum entrants allowed",
                value = waitlistCap,
                onValueChange = onWaitlistChange
            )
        }
    }
}

/** The largest number a quota field accepts. */
private const val MAX_QUOTA = 99_999

/** A labelled card with a typed number between minus and plus buttons. */
@Composable
private fun QuotaRow(
    label: String,
    description: String?,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    val stepperColors = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )
    val focusManager = LocalFocusManager.current

    // Kept apart from value so the field can be empty while typing.
    var typedText by rememberSaveable { mutableStateOf(value.toString()) }
    val fieldText = if (typedText.isEmpty() && value == 0 || typedText.toIntOrNull() == value) {
        typedText
    } else {
        value.toString()
    }

    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    FilledTonalIconButton(
                        onClick = { onValueChange(value - 1) },
                        enabled = value > 0,
                        colors = stepperColors,
                        modifier = Modifier
                            .size(36.dp)
                            .semantics { contentDescription = "Decrease $label" }
                    ) {
                        Text("−", fontWeight = FontWeight.Bold)
                    }
                    BasicTextField(
                        value = fieldText,
                        onValueChange = { input ->
                            val digits = input.filter { it.isDigit() }
                                .take(MAX_QUOTA.toString().length)
                            typedText = digits.trimStart('0')
                                .ifEmpty { if (digits.isEmpty()) "" else "0" }
                            onValueChange(typedText.toIntOrNull() ?: 0)
                        },
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                        modifier = Modifier
                            .width(56.dp)
                            .onFocusChanged { focus ->
                                if (!focus.isFocused && typedText.isEmpty()) typedText = "0"
                            }
                            .semantics { contentDescription = label }
                    )
                    FilledTonalIconButton(
                        onClick = { onValueChange(value + 1) },
                        enabled = value < MAX_QUOTA,
                        colors = stepperColors,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Increase $label",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PopUpComponent(
    campsiteNames: List<String>,
    lotteryEnd: Date,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDone) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFDFF3E4), CircleShape)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color(0xFF1B5E20),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Lottery created",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Your lottery for ${joinNames(campsiteNames)} is live. " +
                                    "Campers can enter until ${formatDate(lotteryEnd)}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onDone,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF111827),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** The outlined card with a title and optional subtitle used by each section. */
@Composable
private fun SectionCard(
    title: String,
    subtitle: String?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

/** How far ahead a lottery date can be set. */
private const val MAX_DAYS_AHEAD = 730

/** Every calendar day from [start] to [end], including both, at midnight. */
private fun datesBetween(start: Date, end: Date): List<Date> {
    val days = mutableListOf<Date>()
    var day = start.toDayOnly()
    val lastDay = end.toDayOnly()
    while (!day.after(lastDay)) {
        days += day
        day = addDays(day, 1)
    }
    return days
}

/** The day [count] days after [date], at midnight. */
private fun addDays(date: Date, count: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.time = date.toDayOnly()
    calendar.add(Calendar.DAY_OF_MONTH, count)
    return calendar.time
}

/** Formats a date like "May 1, 2027". */
private fun formatDate(date: Date): String =
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date)

private fun Date.toYearMonth(): YearMonth {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return YearMonth.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
}

/**
 * Turns calendar taps into one date range. With one day already picked, the tapped day
 * ends the range. Otherwise, the tapped day starts a new range.
 */
private fun nextRange(days: Set<Date>, tapped: Date): Set<Date> {
    if (days.size != 1) return setOf(tapped)
    val first = days.first()
    return datesBetween(minOf(first, tapped), maxOf(first, tapped)).toSet()
}

/** "2 Nights Selected", or a prompt if only the first day of the range is picked. */
private fun nightsLabel(days: Set<Date>): String {
    val nights = days.size - 1
    return when (nights) {
        0 -> "Choose end date"
        1 -> "1 Night Selected"
        else -> "$nights Nights Selected"
    }
}

/** Formats days like "May 17 – May 19, 2026", or blank if there are none. */
private fun formatDayRange(days: Set<Date>): String {
    val first = days.minOrNull() ?: return ""
    val last = days.maxOrNull() ?: return ""
    if (first == last) return formatDate(first)

    val sameYear = first.toYearMonth().year == last.toYearMonth().year
    val firstText = if (sameYear) {
        SimpleDateFormat("MMM d", Locale.getDefault()).format(first)
    } else {
        formatDate(first)
    }
    return "$firstText – ${formatDate(last)}"
}

/** Joins names like "A", "A and B", or "A, B and C". */
private fun joinNames(names: List<String>): String =
    if (names.size < 2) {
        names.joinToString()
    } else {
        names.dropLast(1).joinToString(", ") + " and " + names.last()
    }