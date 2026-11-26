/**
 * This file contains the seperated UI components of the lottery creation screen
 */
package com.example.campsitereservationapp.ui.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.components.StatusBadge
import com.example.campsitereservationapp.utils.decodeBase64Image
import java.util.Date

/**
 * The parts of the lottery screen that only appear when editing an existing lottery: the summary
 * card at the top, the Danger section with the cancel button, and the confirm-cancel dialog.
 *
 * Outstanding issues:
 *  - Cancelling marks the lottery as cancelled; entrants on its waitlist are not notified yet
 *    because the app has no notifications.
 *
 * [LotterySummaryCard] itself is the card at the top of the edit screen: the first campsite's
 * photo and address, every campsite's name, how many entrants are enrolled, and a status badge.
 */
@Composable
internal fun LotterySummaryCard(
    lottery: Lottery,
    campsites: List<Listing>,
    modifier: Modifier = Modifier,
    now: Date = Date()
) {
    val enrolled = enrolledCount(lottery)
    val firstCampsite = campsites.firstOrNull()

    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AsyncImage(
                model = decodeBase64Image(firstCampsite?.photoUrls?.firstOrNull()),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (firstCampsite != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(2.dp))
                        Text(
                            firstCampsite.address,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Text(
                    campsites.joinToString(", ") { it.name }.ifEmpty { "No campsites" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "$enrolled Enrolled ${if (enrolled == 1) "Entrant" else "Entrants"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            LotteryStateBadge(lottery, now)
        }
    }
}

/** Pill showing whether registration is open ("Active Draw"), not open yet, or closed. */
@Composable
private fun LotteryStateBadge(lottery: Lottery, now: Date) {
    val colors = MaterialTheme.colorScheme
    val (text, container, content) = when {
        now.after(lottery.end) -> Triple("Closed", colors.surfaceVariant, colors.onSurfaceVariant)
        now.before(lottery.start) -> Triple("Upcoming", colors.tertiaryContainer, colors.onTertiaryContainer)
        else -> Triple("Active Draw", colors.secondaryContainer, colors.onSecondaryContainer)
    }
    StatusBadge(text = text, containerColor = container, contentColor = content)
}

/**
 * Red "Danger" box with the Cancel Lottery Draw button. It only asks for confirmation;
 * [onCancelClick] should open [CancelLotteryDialog].
 */
@Composable
internal fun CancelLotterySection(
    enrolledCount: Int,
    onCancelClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Surface(
        shape = MaterialTheme.shapes.large,
        color = colors.errorContainer.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, colors.error.copy(alpha = 0.3f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = colors.error)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Danger",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.error
                )
            }
            Text(
                "Cancelling this lottery ends the draw and moves it to Cancelled. This can't be undone. A notification will be sent" +
                        " to all entrants when the lottery is cancelled.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )
            FilledTonalButton(
                onClick = onCancelClick,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = colors.errorContainer,
                    contentColor = colors.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Outlined.EventBusy, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Cancel Lottery Draw", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Asks the owner to confirm before the lottery is cancelled.
 * @param onConfirm event call when the owner confirms; cancels the lottery
 * @param onDismiss event call when the owner keeps the lottery or taps outside
 */
@Composable
internal fun CancelLotteryDialog(
    lotteryTitle: String,
    enrolledCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
        title = { Text("Cancel this lottery?") },
        text = {
            Text(
                "\"${lotteryTitle.ifBlank { "This lottery" }}\" will be cancelled and its $enrolledCount " +
                    "${if (enrolledCount == 1) "entrant" else "entrants"} won't be drawn. This can't be undone."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cancel Lottery", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Keep Lottery")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.extraLarge
    )
}

/** Entrants still waiting plus entrants already drawn. */
internal fun enrolledCount(lottery: Lottery): Int = lottery.waitlist.size + lottery.selectedlist.size
