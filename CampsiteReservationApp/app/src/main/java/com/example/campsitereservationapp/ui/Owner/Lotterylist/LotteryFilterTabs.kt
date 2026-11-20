package com.example.campsitereservationapp.ui.Owner.Lotterylist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.ui.theme.PillShape

/**
 * Row of pill tabs ("All (4)", "Active (1)", ...) that filters the lottery list according to which one is selected.
 *
 * @param counts how many lotteries fall under each tab.
 */
@Composable
internal fun LotteryFilterTabs(
    selected: LotteryFilter,
    counts: Map<LotteryFilter, Int>,
    onSelect: (LotteryFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(LotteryFilter.entries) { filter ->
            FilterTab(
                text = "${filter.label} (${counts[filter] ?: 0})",
                selected = filter == selected,
                onClick = { onSelect(filter) }
            )
        }
    }
}

/** One pill tab. */
@Composable
private fun FilterTab(text: String, selected: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = PillShape,
        color = if (selected) colors.onSurface else colors.surfaceVariant,
        contentColor = if (selected) colors.surface else colors.onSurfaceVariant,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else null,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
