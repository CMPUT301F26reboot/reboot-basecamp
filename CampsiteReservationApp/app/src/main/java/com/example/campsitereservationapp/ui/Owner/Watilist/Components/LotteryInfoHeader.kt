package com.example.campsitereservationapp.ui.Owner.Watilist.Components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.ui.Owner.LotteryCreation.components.DateRangeCalendarDialog
import com.example.campsitereservationapp.ui.Owner.LotteryCreation.components.InputDateScreenComponent
import com.example.campsitereservationapp.ui.Owner.LotteryCreation.components.formatDateRange
import com.example.campsitereservationapp.ui.Owner.LotteryCreation.components.nextDateRange
import java.util.Date

/**
 * A reusable input composable for getting a range of dates from the user.
 * The caller owns the dates: this composable reads them and reports each tapped day.
 *
 * @param title title of lottery
 * @param locationName [String] location of lottery
 * @param formatedDates [String] already formated dates of the lottery
 */

@Composable
fun LotteryInfoHeader(
    title: String? = null,
    locationName: String? = null,
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
            if (locationName != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(20.dp)
                ){
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Choose dates",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (title != null || locationName != null) {
                Spacer(Modifier.height(12.dp))
            }



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
        locationName = "Location of Lottery",
        formatedDates = "Dates - Dates , Date"
    )


}
