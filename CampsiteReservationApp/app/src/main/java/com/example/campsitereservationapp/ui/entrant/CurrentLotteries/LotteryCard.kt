/**
 * This file contains the lottery card used in currentLotteryScreen
 *
 * AI USE: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-27_UserCurrentLotteryCard.md
 */
package com.example.campsitereservationapp.ui.entrant.CurrentLotteries

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.owner.Lotterylist.formatDateSpan
import com.example.campsitereservationapp.ui.theme.DeleteRed
import com.example.campsitereservationapp.ui.theme.commonButtonColour

/**
 * Lottery Card used in UserCurrentLotteries. Replaces WonLotteryCard and OngoingLotteryCard.
 * Shows the lottery's details in the same format as OwnerLotteryCard, with one button at the
 * bottom: a red "Leave Lottery" button, or a "Claim" button in [commonButtonColour].
 * @param lottery lottery being shown
 * @param imageURL first image in lottery, or "" for none
 * @param profileId document id of the entrant's profile, passed to [onClick]
 * @param buttonLabel either "Leave Lottery" or "Claim"
 * @param onClick action when the button is pressed, given [lottery] and [profileId]
 */
@Composable
fun UserLotteryCard(
    lottery: Lottery,
    imageURL: String,
    profileId: String,
    buttonLabel: String,
    onClick: (Lottery, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(fraction = 0.9f),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            UserLotteryCardHeader(lottery, imageURL)
            UserLotteryDetails(lottery)
            EntrantsJoinedRow(
                entrantCount = lottery.waitlist.size + lottery.selectedlist.size,
                waitlistCap = lottery.waitlistCapacity
            )
            LeaveLotteryOrClaimButton(buttonLabel, onClick = { onClick(lottery, profileId) })
        }
    }
}

/** Lottery image (or a calendar icon when there is none) and the title. */
@Composable
private fun UserLotteryCardHeader(lottery: Lottery, imageURL: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentAlignment = Alignment.Center
        ) {
            if (imageURL.isNotEmpty()) {
                AsyncImage(
                    model = imageURL,
                    contentDescription = lottery.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            lottery.title.ifBlank { "Untitled lottery" },
            style = MaterialTheme.typography.titleLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

/** Registration window and booking stay, in a light gray box. */
@Composable
private fun UserLotteryDetails(lottery: Lottery) {
    val stayStart = lottery.dates.minOrNull()
    val stayEnd = lottery.dates.maxOrNull()
    val stay = if (stayStart != null && stayEnd != null) {
        formatDateSpan(stayStart, stayEnd)
    } else {
        "No dates set"
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DetailRow("Registration Window:", formatDateSpan(lottery.start, lottery.end))
            DetailRow("Booking Stay:", stay)
        }
    }
}

/** A gray label on the left and its value on the right. */
@Composable
private fun DetailRow(label: String, value: String) {
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

/** Full-width button: red for "Leave Lottery", [commonButtonColour] for "Claim". */
@Composable
private fun LeaveLotteryOrClaimButton(buttonLabel: String, onClick: () -> Unit) {
    val containerColor = if (buttonLabel == "Leave Lottery") DeleteRed else commonButtonColour
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(buttonLabel, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}