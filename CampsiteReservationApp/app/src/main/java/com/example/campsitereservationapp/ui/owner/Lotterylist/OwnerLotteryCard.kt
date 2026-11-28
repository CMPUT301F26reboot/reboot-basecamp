/**
 * This file contains a separated UI component for OwnerLotteryListScreen
 */
package com.example.campsitereservationapp.ui.owner.Lotterylist

import com.example.campsitereservationapp.utils.appDateFormat
import com.example.campsitereservationapp.utils.appCalendar
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.ui.components.StatusBadge
import java.util.Calendar
import java.util.Date

/**
 * One lottery in the owner's list. Every card shows the title, campsite and a status badge, then
 * the details and buttons that fit its [OwnerLotteryItem.status]:
 *  - Active: registration window, stay, draw rule, entrants joined, and View Entrants / Edit
 *    buttons.
 *  - Completed: stay and draw results, with Entrants and Results buttons.
 *  - Cancelled: only the title, campsite and "Cancelled" badge.
 *
 * On a lottery the owner joined from another owner's invitation, the details also say who hosts
 * it, and the Edit icon is replaced by a Withdraw button, since only the creator can edit.
 */
@Composable
fun OwnerLotteryCard(
    lottery: OwnerLotteryItem,
    displayButtons: Boolean,
    onViewEntrants: () -> Unit,
    onEdit: () -> Unit,
    onViewResults: () -> Unit,
    modifier: Modifier = Modifier,
    onWithdraw: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LotteryCardHeader(lottery)
            when (lottery.status) {
                LotteryStatus.ACTIVE -> {
                    ActiveDetails(lottery)
                    EntrantsJoinedRow(lottery.entrantCount, lottery.waitlistCap)
                    if (displayButtons) {
                        if (lottery.isCreator) {
                            ActiveActions(lottery.entrantCount, onViewEntrants, onEdit)
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                SecondaryButton("View Entrants (${lottery.entrantCount})", onViewEntrants, Modifier.weight(1f))
                                SecondaryButton("Withdraw Campsite", onWithdraw, Modifier.weight(1f))
                            }
                        }
                    }
                }
                LotteryStatus.COMPLETED -> {
                    CompletedDetails(lottery)
                    if(displayButtons) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SecondaryButton("Entrants", onViewEntrants, Modifier.weight(1f))
                            SecondaryButton("Results", onViewResults, Modifier.weight(1f))
                        }
                    }
                }
                LotteryStatus.CANCELLED -> Unit
            }
        }
    }
}

/** Title, campsite name with a pin icon, and the status badge on the right. */
@Composable
private fun LotteryCardHeader(lottery: OwnerLotteryItem) {
    Row(verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                lottery.title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    lottery.campsiteName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        LotteryStatusBadge(lottery)
    }
}

/** Pill badge: green countdown when active, gray "Completed", or red "Cancelled". */
@Composable
private fun LotteryStatusBadge(lottery: OwnerLotteryItem) {
    val colors = MaterialTheme.colorScheme
    val (text, container, content) = when (lottery.status) {
        LotteryStatus.ACTIVE -> Triple(lottery.timeLeftLabel, colors.secondaryContainer, colors.onSecondaryContainer)
        LotteryStatus.COMPLETED -> Triple("Completed", colors.surfaceVariant, colors.onSurfaceVariant)
        LotteryStatus.CANCELLED -> Triple("Cancelled", colors.errorContainer, colors.error)
    }
    StatusBadge(text = text, containerColor = container, contentColor = content)
}

/** Details for an open lottery: registration window, stay and draw rule. */
@Composable
private fun ActiveDetails(lottery: OwnerLotteryItem) {
    DetailsBox {
        if (!lottery.isCreator) DetailRow("Hosted by:", lottery.hostName)
        DetailRow("Registration Window:", formatDateSpan(lottery.registrationStart, lottery.registrationEnd))
        DetailRow("Booking Stay:", formatStay(lottery))
        DetailRow("Draw Rule:", drawRule(lottery.winnersToDraw, lottery.waitlistCap))
    }
}

/** Details for a finished lottery: stay, and how many won out of how many entries. */
@Composable
private fun CompletedDetails(lottery: OwnerLotteryItem) {
    val results = if (lottery.winnersDrawn == 0) {
        "Not drawn yet • ${lottery.entrantCount} entries"
    } else {
        "${winnerCount(lottery.winnersDrawn)} from ${lottery.entrantCount} entries"
    }
    DetailsBox {
        if (!lottery.isCreator) DetailRow("Hosted by:", lottery.hostName)
        DetailRow("Booking Stay:", formatStay(lottery))
        DetailRow("Draw Results:", results)
    }
}

/** Light gray rounded box that groups a card's detail rows. */
@Composable
private fun DetailsBox(content: @Composable () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

/** A gray label on the left and its value on the right. */
@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(12.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

/** "Entrants Joined" with the number joined, out of the cap when there is one. */
@Composable
private fun EntrantsJoinedRow(entrantCount: Int, waitlistCap: Int?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Outlined.Group,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text("Entrants Joined", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Text(
            if (waitlistCap != null) "$entrantCount / $waitlistCap" else "$entrantCount",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Buttons for an open lottery: View Entrants and an Edit icon. */
@Composable
private fun ActiveActions(
    entrantCount: Int,
    onViewEntrants: () -> Unit,
    onEdit: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onViewEntrants,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text("View Entrants ($entrantCount)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
        FilledTonalIconButton(
            onClick = onEdit,
            shape = MaterialTheme.shapes.medium,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Icon(Icons.Filled.Edit, contentDescription = "Edit lottery", modifier = Modifier.size(18.dp))
        }
    }
}

/** Light gray button used for the secondary actions on every card. */
@Composable
private fun SecondaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        modifier = modifier
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

/** winner count for each lottery card, or 1 winner minimum. */
private fun winnerCount(count: Int): String = if (count == 1) "1 Winner" else "$count Winners"

/** "2 Winners • Waitlist Cap: 50", leaving out whichever part isn't set. */
private fun drawRule(winnersToDraw: Int?, waitlistCap: Int?): String =
    listOfNotNull(
        winnersToDraw?.let(::winnerCount),
        waitlistCap?.let { "Waitlist Cap: $it" }
    ).joinToString(" • ").ifEmpty { "Not set" }

/** The stay's date range, or "No dates set" when the lottery has none. */
private fun formatStay(lottery: OwnerLotteryItem): String {
    val start = lottery.stayStart ?: return "No dates set"
    return formatDateSpan(start, lottery.stayEnd ?: start)
}

/**
 * Formats a date range like "May 17 – May 19, 2025", or "Dec 30, 2025 – Jan 2, 2026" when the
 * years differ. Named differently from the list-based `formatDateRange` in `ui/components`.
 */
internal fun formatDateSpan(start: Date, end: Date): String {
    val sameYear = yearOf(start) == yearOf(end)
    val startPattern = if (sameYear) "MMM d" else "MMM d, yyyy"
    val startText = appDateFormat(startPattern).format(start)
    val endText = appDateFormat("MMM d, yyyy").format(end)
    return "$startText – $endText"
}

/** The calendar year [date] falls in. */
private fun yearOf(date: Date): Int = appCalendar().apply { time = date }.get(Calendar.YEAR)
