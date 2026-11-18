package com.example.campsitereservationapp.ui.entrant.Notifications

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import java.text.SimpleDateFormat
import java.util.Locale

// Note: Code structure was inspired by Jonathan's Reservations screen code

/**
 * User Notifications screen —
 *
 * US 01.07.02 (Entrant Cancellation Confirmation)
 * US 01.05.01 (Entrant Reservation Confirmation)
 * US 01.05.02 (Entrant Reservation Notification/CSV)
 * US 01.03.01 (Entrant Lottery Won Notification)
 * US 01.03.02 (Entrant Lottery Loss Notification)
 *
 * The entrant's Notifications screen.
 *
 * Split into two parts: [UserNotificationsScreen] is the version that talks to
 * the ViewModel, and [UserNotificationsContent] is the UI version that just takes
 * values and callbacks.
 *
 * @param profileId the current user's Firestore document ID. Used to filter notifications by `entrantID`.
 * @param onBack called when the back button is tapped.
 * @param onOpenNotif called with when user clicks a notification.
 * @param viewModel defaults to the real Compose [viewModel], can be swapped out for testing.
 */
@Composable
fun UserNotificationsScreen(
    profileId: String,
    onBack: () -> Unit,
    onOpenNotif: () -> Unit,
    viewModel: UserNotificationsViewModel = viewModel()
){
    val rows = viewModel.filteredNotifications(profileId)

    UserNotificationsContent(
        rows = rows,
        onBack = onBack,
        onOpen = onOpenNotif,
    )
}

/**
 * Stateless version of the screen. Everything it shows comes from parameters, and every
 * tap goes back out through a callback.
 *
 * @param rows the notifications associated with users
 */
@Composable
fun UserNotificationsContent(
    rows: List<Notifications>,
    onBack: () -> Unit,
    onOpen: () -> Unit,
) {
    var search by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            BackButtonAndHeaderScreenComponent(
                title = "Notifications",
                subHeader = "Notifications you've recieved",
                onBack = onBack
            )

            //search notifications
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search Notifications") },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )

            // TODO: add filter by date feature here

            Spacer(Modifier.height(8.dp))
            //if no reservations
            if (rows.isEmpty()) {
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
                    items(rows, key = { it.id }) { notification ->
                        NotificationCard(
                            notification = notification,
                            isViewed = false,
                            onOpen = { onOpen() },
                        )
                    }
                }
            }
        }
    }
}


/**
 * One notification card: subject, sender, timestamp, message, and greyed out if it has been viewed.
 * Clicking will either take user to reservations screen, or view the reservation info (cancelled/accepted)
 */
@Composable
private fun NotificationCard(
    notification: Notifications,
    isViewed: Boolean,
    onOpen: () -> Unit
) {
    Surface(
        color = if(isViewed){ MaterialTheme.colorScheme.surfaceVariant} else{MaterialTheme.colorScheme.surface},
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // campsite details
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
                    Text(
                        notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // add button that performs different things depending on the notification type
        }
    }
}
