// Admin notifications screen. Displays the UI for the notifications screen, where the admin
// can search through the notifications, click on the sender/recipient to view their profile, and delete the notification

/**
 * Admin Notifications screen —
 *
 * US 03.08.01 (Admin Notification Logs)
 *
 * The admin's Notifications screen. Lists every notification, newest notifications appear first, and they can
 * delete the notification by clicking the trash button
 *
 * Split into two parts: [AdminNotificationLogsScreen] is the version that talks to
 * the ViewModel, and [AdminNotificationLogsViewModel] is the UI version that just takes
 * values and callbacks.
 */

package com.example.campsitereservationapp.ui.Admin.Notifications

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import java.text.SimpleDateFormat
import java.util.Locale

// Note: Code structure was inspired/copied from the owner notifications screen

/**
 * Entry point for the admin notifications screen. Connects [AdminNotificationLogsViewModel] to
 * [AdminNotificationLogsContent].
 *
 * @param onBack called when the back button is tapped.
 * @param viewModel the screen's ViewModel; can be replaced for testing.
 */
@Composable
fun AdminNotificationLogsScreen(
    onBack: () -> Unit,
    viewModel: AdminNotificationLogsViewModel = viewModel()
){

    AdminNotificationLogsContent(
        onBack = onBack,
        viewModel = viewModel
    )
}

/**
 * The Notifications screen layout: search bar, and the list of notification cards.
 * Shows all of the notifications until something is typed, then updates the list
 * as the search text changes.
 *
 * @param onBack called when the back button is tapped.
 * @param viewModel provides the search results and runs searches.
 */
@Composable
fun AdminNotificationLogsContent(
    onBack: () -> Unit,
    viewModel: AdminNotificationLogsViewModel,
) {
    var search by remember { mutableStateOf("") }

    LaunchedEffect(viewModel.allNotifications.size) {
        viewModel.doSearch(search)
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
                title = "Notification Logs",
                subHeader = "All notifications sent from system or owners.",
                onBack = onBack
            )

            //search notifications
            OutlinedTextField(
                value = search,
                onValueChange = {
                    search = it
                    viewModel.doSearch(it)
                },
                placeholder = { Text("Search Notification log") },
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
                            viewModel = viewModel,
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
 */
@Composable
private fun NotificationCard(
    notification: Notifications,
    viewModel: AdminNotificationLogsViewModel,
) {
    Surface(
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
                        text = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault()).format(
                            notification.timestamp
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Sender: " + viewModel.getSenderName(notification.senderId),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.Underline,
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}