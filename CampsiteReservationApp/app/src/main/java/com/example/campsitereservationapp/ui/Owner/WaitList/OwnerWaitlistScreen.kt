/**
 * Screen to view entrants in waitlist and entrants who won
 */
package com.example.campsitereservationapp.ui.owner.WaitList

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.Admin.HomeScreenComponents.HomeScreenButton
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent


/**
 * Displays Some Lottery Information and entrants, also if an entrant is in the waitlist
 * or won the booking
 * @param lotteryID ID of whatever lottery is to be shown
 * @param onBack navigate to previous screen
 * @param onSendNotification event call to send notifications to all entrants
 * @param viewModel, do not change, calles OwnerWaitlistViewModel
 */
@Composable
fun OwnerWaitlistScreen(
    lotteryID: String,
    onBack: () -> Unit = {},
    onSendNotification: () -> Unit = {},
    viewModel: OwnerWaitlistViewModel = viewModel()

){
    //get needed information
    val lottery = viewModel.getLottery(lotteryID)
    val registrationWindowDates = viewModel.getFormatedRegistrationWindowDates(lottery)
    val bookingStayDates = viewModel.getFormatedBookingStayDatesDates(lottery)
    val waitlist = lottery?.waitlist ?: emptyList()
    val selectedlist = lottery?.selectedlist?: emptyList()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ){
        BackButtonAndHeaderScreenComponent(
            title = "Lottery Waitlist",
            subHeader = "View and notify people waiting for this draw",
            onBack = onBack

        )

        Spacer(Modifier.height(24.dp))

        LotteryInfoHeader(
            title = lottery?.title,
            registrationWindowDates = registrationWindowDates,
            bookingStayDates = bookingStayDates

        )

        Spacer(Modifier.height(24.dp))

        Text(//Header for name list -> Entrants (#of people)
            text = "Entrants (${waitlist.size + selectedlist.size})",
            style = TextStyle( // altered headline medium
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp
            )
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = "User name",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "Status",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(12.dp))

        //Entrant list
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //show winners first
            itemsIndexed(selectedlist) { index, entrantID ->
                val name = viewModel.getEntrantName(entrantID)
                WaitListCard(
                    position = index + 1,
                    name = name,
                    won = true
                )
            }
            //rest of waitlist
            itemsIndexed(waitlist) { index, entrantID ->
                val name = viewModel.getEntrantName(entrantID)
                WaitListCard(
                    position = index + 1 + selectedlist.size,
                    name = name
                )
            }

        }

        //notification button
        HomeScreenButton(
            text = "Send Notifications to Entrants",
            darkened = true,
            onClick = onSendNotification
        )

    }



}