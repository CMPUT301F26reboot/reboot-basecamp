package com.example.campsitereservationapp.ui.owner.WaitList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


/**
 * A reusable input composable for getting a range of dates from the user.
 * The caller owns the dates: this composable reads them and reports each tapped day.
 *
 * @param title title of lottery
 * @param formatedDates [String] already formated dates of the lottery
 */

@Composable
fun LotteryInfoHeader(
    title: String? = null,
    formatedDates: String? = null,
    modifier: Modifier = Modifier,

    ){
    var showCalendar by rememberSaveable { mutableStateOf(false) }

    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            if (formatedDates != null) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "Booking Stay:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = formatedDates,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

}


//Preview of where each variable string goes
@Preview(showBackground = true)
@Composable
fun LotteryInfoHeaderPreview(){
    LotteryInfoHeader(
        title = "Title of Lottery",
        formatedDates = "Dates - Dates , Date"
    )


}