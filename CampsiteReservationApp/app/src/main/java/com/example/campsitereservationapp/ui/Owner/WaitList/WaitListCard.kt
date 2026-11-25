/**
 * The UI card to display the name of people in the waitlist
 *
 * AI Reference: AI Use Logs - Jacob/ Claude-Chat-Log_2026-11-24_OwnerWaitList.md
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campsitereservationapp.ui.theme.Gray300
import com.example.campsitereservationapp.ui.theme.Gray600

@Composable
fun WaitListCard(
    position: Int,
    name: String,
    formatedDates: String? = null,
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

            // Name, with dates underneath if given
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (formatedDates != null) {
                    Text(
                        text = formatedDates,
                        fontSize = 13.sp,
                        color = Gray600
                    )
                }
            }

        }
        HorizontalDivider(color = Gray600)
    }
}