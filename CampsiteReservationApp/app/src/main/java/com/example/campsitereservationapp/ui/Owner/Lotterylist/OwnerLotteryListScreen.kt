package com.example.campsitereservationapp.ui.owner.Lotterylist

import com.example.campsitereservationapp.utils.appCalendar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import com.example.campsitereservationapp.ui.theme.PillShape
import java.util.Calendar
import java.util.Date

/**
 * Lists the owner's lotteries with tabs to filter by status, and a button to create
 * a new lottery. Stateful wrapper that wires [OwnerLotteryListViewModel] to
 * [OwnerLotteryListContent].
 *
 * @param ownerId the current owner. Comes from device identification once that exists.
 * @param onCreateLottery event -> navigate to lottery creation.
 * @param onViewEntrants event -> navigate to the entrants of the lottery with the given ID.
 * @param onEditLottery event -> navigate to editing the lottery with the given ID.
 * @param onViewResults event -> navigate to the draw results of the lottery with the given ID.
 *
 * Outstanding issues:
 *  - The entrants, results and edit screens don't exist yet, so those buttons do nothing.
 */
@Composable
fun OwnerLotteryListScreen(
    ownerId: String,
    onBack: () -> Unit,
    onCreateLottery: () -> Unit,
    onViewEntrants: (String) -> Unit,
    onEditLottery: (String) -> Unit,
    onViewResults: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OwnerLotteryListViewModel = viewModel()
) {
    OwnerLotteryListContent(
        lotteries = viewModel.ownerLotteries(ownerId),
        selectedFilter = viewModel.selectedFilter,
        onSelectFilter = viewModel::selectFilter,
        onBack = onBack,
        onCreateLottery = onCreateLottery,
        onViewEntrants = onViewEntrants,
        onEditLottery = onEditLottery,
        onViewResults = onViewResults,
        modifier = modifier
    )
}

/**
 * UI for the lottery list. Stateless so it can be driven entirely by its parameters in a
 * Compose Preview, with no database involved.
 *
 * @param lotteries every lottery the owner has; the tabs filter this list.
 */
@Composable
fun OwnerLotteryListContent(
    lotteries: List<OwnerLotteryItem>,
    selectedFilter: LotteryFilter,
    onSelectFilter: (LotteryFilter) -> Unit,
    onBack: () -> Unit,
    onCreateLottery: () -> Unit,
    onViewEntrants: (String) -> Unit,
    onEditLottery: (String) -> Unit,
    onViewResults: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val counts = LotteryFilter.entries.associateWith { filter -> lotteries.count(filter::matches) }
    val shown = lotteries.filter(selectedFilter::matches)

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            CreateLotteryButton(
                onClick = onCreateLottery,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BackButtonAndHeaderScreenComponent(
                    title = "Owner Lotteries",
                    onBack = onBack,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            item {
                LotteryFilterTabs(
                    selected = selectedFilter,
                    counts = counts,
                    onSelect = onSelectFilter
                )
            }
            if (shown.isEmpty()) {
                item { EmptyLotteries(selectedFilter) }
            }
            items(shown, key = { it.id }) { lottery ->
                OwnerLotteryCard(
                    lottery = lottery,
                    onViewEntrants = { onViewEntrants(lottery.id) },
                    onEdit = { onEditLottery(lottery.id) },
                    onViewResults = { onViewResults(lottery.id) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

/** Message shown when no lottery matches the selected tab. */
@Composable
private fun EmptyLotteries(filter: LotteryFilter) {
    val text = if (filter == LotteryFilter.ALL) {
        "No lotteries yet. Create one to start taking entrants."
    } else {
        "No ${filter.label.lowercase()} lotteries."
    }
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 32.dp)
    )
}

/** Dark pill button at the bottom that starts creating a new lottery. */
@Composable
private fun CreateLotteryButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("Create New Lottery Event", fontWeight = FontWeight.Bold)
    }
}

/** Date [daysFromToday] days from now, at the current time of day. Used by the preview. */
private fun previewDate(daysFromToday: Int, hours: Int = 0): Date =
    appCalendar().apply {
        add(Calendar.DAY_OF_YEAR, daysFromToday)
        add(Calendar.HOUR_OF_DAY, hours)
    }.time

