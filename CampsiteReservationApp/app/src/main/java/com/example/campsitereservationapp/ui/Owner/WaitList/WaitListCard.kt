/**
 * The UI card to display the name of people in the waitlist
 *
 * AI Reference: AI Use Logs - Jacob/ Claude-Chat-Log_2026-11-24_OwnerWaitList.md (mostly scrapped but
 * used as starting code)
 */

package com.example.campsitereservationapp.ui.owner.WaitList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campsitereservationapp.ui.theme.Gray300
import com.example.campsitereservationapp.ui.theme.Gray600
import com.example.campsitereservationapp.ui.theme.RatingAmber

/**
 * Reused UI card for list of entrants
 * @param position number in circle shown on left side of card, calculated in Screen
 * @param name name of entrant
 * @param won [Boolean] status of entrant, either they won or is waiting
 */
@Composable
fun WaitListCard(
    position: Int,
    name: String,
    won: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Numbered gray circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Gray300, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = position.toString(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.weight(1f))

            if (won){
                Text(
                    text = "Won",
                    style = MaterialTheme.typography.bodyLarge,
                    color = RatingAmber
                )
            }else{
                Text(
                    text = "Waiting",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        HorizontalDivider(color = Gray600)
    }
}