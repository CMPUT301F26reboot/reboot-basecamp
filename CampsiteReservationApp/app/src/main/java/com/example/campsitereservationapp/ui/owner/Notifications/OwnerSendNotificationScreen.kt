// Owner notifications screen. Displays the UI for the owner sending a notification screen

/**
 * Owner Send Notifications screen —
 *
 * US 02.10.01 (Campsite Owner Notify Waitlist)
 *
 * The owner Send Notifications screen. Allows the owner to send a notification to all entrants of
 * a particular lottery. Screen displays the lottery info (name, location, lottery length, reservation length),
 * allows the owner to type a subject line, and then a message box.
 *
 * Split into two parts: [OwnerSendNotificationScreen] is the version that talks to
 * the ViewModel, and [OwnerSendNotificationContent] is the UI version that just takes
 * values and callbacks.
 */

package com.example.campsitereservationapp.ui.owner.Notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import com.example.campsitereservationapp.ui.components.CustomDialogScreenComponent
import com.example.campsitereservationapp.ui.owner.Lotterylist.OwnerLotteryCard
import com.example.campsitereservationapp.ui.owner.Lotterylist.OwnerLotteryListViewModel
import com.example.campsitereservationapp.ui.theme.PillShape

/**
 * Entry point for the owner notifications screen. Connects [OwnerSendNotificationScreen] to
 * [OwnerSendNotificationContent].
 *
 * @param profileId the current owners profile ID, used to add them as the sender when creating notif
 * @param lotteryId the lottery ID of the selected lottery the owner wants to send notifications to
 * @param onBack called when the back button is tapped.
 * @param viewModel the screen's ViewModel; can be replaced for testing.
 */
@Composable
fun OwnerSendNotificationScreen(
    profileId: String,
    lotteryId: String,
    onBack: () -> Unit,
    viewModel: OwnerSendNotificationViewModel = viewModel()
){

    OwnerSendNotificationContent(
        profileId = profileId,
        lotteryId = lotteryId,
        onBack = onBack,
        viewModel = viewModel
    )
}

/**
 * The Notifications screen layout: lottery information, whose being sent (everyone), subject box, message box
 * and send button
 *
 * @param profileId the current owners profile ID.
 * @param lotteryId the lottery ID of the lottery waitlist to send notifs to
 * @param onBack called when the back button is tapped.
 * @param viewModel provides the search results and runs searches.
 */
@Composable
fun OwnerSendNotificationContent(
    profileId: String,
    lotteryId: String,
    onBack: () -> Unit,
    viewModel: OwnerSendNotificationViewModel
) {

    var showCreatedDialog by remember { mutableStateOf(false) }
    var showFailedDialog by remember { mutableStateOf(false) }

    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    // Lottery list screen already has a really nice lottery card display, so will borrow
    // it's viewModel so that we can display our lottery the same way
    val lotteryListViewModel: OwnerLotteryListViewModel = viewModel()
    val lottery = lotteryListViewModel.ownerLotteries(profileId).firstOrNull { it.id == lotteryId }

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            Button(
                onClick = {
                    viewModel.onSend(
                        subject,
                        message,
                        profileId,
                        lotteryId,
                        onSuccess = {
                            subject = ""
                            message = ""
                            showCreatedDialog = true
                        },
                        onFailure = { showFailedDialog = true }
                    )
                },
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Icon(Icons.Filled.Notifications, contentDescription = "Send Notification")
                Spacer(Modifier.width(8.dp))
                Text("Send Notification", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BackButtonAndHeaderScreenComponent(
                title = "Compose Message",
                subHeader = "Send a notification to the waiting list.",
                onBack = onBack
            )

            if(lottery != null){
                OwnerLotteryCard(
                    lottery = lottery,
                    displayButtons = false,
                    onViewEntrants = { },
                    onEdit = { },
                    onViewResults = { },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            else{
                Text("Loading lottery...")
            }

            Spacer(Modifier.height(8.dp))

            Card(){
                Text(
                    "To: All entrants",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                "Subject:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            // Write the subject of the notification
            OutlinedTextField(
                value = subject,
                onValueChange = {
                    subject = it
                },
                placeholder = { Text("Subject...") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                "Message:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            // Write the message of the notification
            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                placeholder = { Text("Message...") },
                minLines = 5,
                singleLine = false,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }

    //popup dialog on successful or failure on listing addition
    if (showCreatedDialog) {
        CustomDialogScreenComponent(
            onDismissRequest = {
                showCreatedDialog = false
            },
            title = "Notification sent!",
            description = "Your notification has been sent. Entrants can view it in their notifications screen."
        )
    } else if (showFailedDialog)
    {
        CustomDialogScreenComponent(
            onDismissRequest = {
                showFailedDialog = false
            },
            title = "Notification creation Failed",

            )
    }

}