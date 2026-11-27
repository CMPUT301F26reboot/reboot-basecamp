// Owner notifications screen. Displays the UI for the notifications screen, where the owner
// can search for the notifications they sent, and click them to go to the send notification screen
// for that lottery

/**
 * Owner Notifications screen —
 *
 * US 02.10.02 (Campsite Owner Sent Notifications Log)
 *
 * The owner's Notifications screen. Lists every notification the owner sent to the entrants
 * of a lottery, newest notifications appear first, and tapping a notification takes the owner to the
 * notification creation screen for the respective lottery.
 *
 * Split into two parts: [OwnerNotifLogScreen] is the version that talks to
 * the ViewModel, and [OwnerNotifLogContent] is the UI version that just takes
 * values and callbacks.
 */

package com.example.campsitereservationapp.ui.Admin.Notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Notifications
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import java.text.SimpleDateFormat
import java.util.Locale

// Note: Code structure was inspired/copied from the user notifications screen

/**
 * Entry point for the owner notifications screen. Connects [OwnerNotifLogViewModel] to
 * [OwnerNotifLogContent].
 *
 * @param profileId the current owners profile ID, used to show only their notifications.
 * @param onBack called when the back button is tapped.
 * @param onOpenNotif called when a notification is tapped with lottery ID, so the caller can navigate to the right waitlist notification.
 * @param viewModel the screen's ViewModel; can be replaced for testing.
 */
@Composable
fun AdminNotificationLogsScreen(
    profileId: String,
    onBack: () -> Unit,
    onOpenNotif: (lotteryId: String) -> Unit,
    viewModel: AdminNotificationLogsViewModel = viewModel()
){

    AdminNotificationLogsContent(
        profileId = profileId,
        onBack = onBack,
        onOpenNotif = onOpenNotif,
        viewModel = viewModel
    )
}

/**
 * The Notifications screen layout: search bar, and the list of notification cards.
 * Shows all of the owners sent notifications until something is typed, then updates the list
 * as the search text changes.
 *
 * @param profileId the current owners profile ID, used when searching.
 * @param onBack called when the back button is tapped.
 * @param onOpenNotif called with a notification's lottery ID when tapped.
 * @param viewModel provides the search results and runs searches.
 */
@Composable
fun AdminNotificationLogsContent(
    profileId: String,
    onBack: () -> Unit,
    onOpenNotif: (lotteryId: String) -> Unit,
    viewModel: AdminNotificationLogsViewModel
) {
    var search by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.allNotifications.size) {
        viewModel.doSearch(search, profileId)
    }

    Scaffold(modifier = Modifier) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BackButtonAndHeaderScreenComponent(
                title = "Notification History",
                subHeader = "Updates you've sent about your lotteries.",
                onBack = onBack
            )

            //search notifications
            OutlinedTextField(
                value = search,
                onValueChange = {
                    search = it
                    viewModel.doSearch(it, profileId)
                },
                placeholder = { Text("Search Notifications") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            //if no notifications
            if (viewModel._searchResults.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Notifications",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(viewModel._searchResults, key = { it.id }) { notification ->
                        NotificationCard(
                            notification = notification,
                            lotteryName = viewModel.getLotName(notification.lotteryId),
                            onClick = {
                                onOpenNotif(notification.lotteryId)
                            },
                        )
                    }
                }
            }
        }
    }
}


/**
 * One notification card showing the subject, time sent, lottery name, and message.
 *
 * @param notification the notification to display.
 * @param lotteryName the name of the lottery the notification belongs to
 * @param onClick called when the card is tapped.
 */
@Composable
private fun NotificationCard(
    notification: Notifications,
    lotteryName: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // notification details
                Column(Modifier.weight(1f)) {
                    Text(
                        notification.subject,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        lotteryName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(
                            notification.timestamp
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}