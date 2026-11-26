/**
 * Deleted in Jeel's PR "entrant-ID"
 */
package com.example.campsitereservationapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.R

/**
 * Temporary start screen for choosing a role while testing. Whether the Admin button shows is
 * decided by the navigation in CampsiteReservationApp, which checks the Firestore `Admins`
 * collection. A "Device ID" line at the bottom is commented out; uncomment it to copy this
 * device's ID into `Admins` when setting up a new admin.
 *
 * @param profileId the ID of this device.
 * @param showAdmin whether to show the Admin button.
 */
@Composable
fun TempNavigationScreen(
    profileId: String,
    showAdmin: Boolean,
    onOwner: () -> Unit,
    onUser: () -> Unit,
    onAdmin: () -> Unit
){
    TempNavigationContent(
        profileId = profileId,
        showAdmin = showAdmin,
        onOwner = onOwner,
        onUser = onUser,
        onAdmin = onAdmin
    )

}

/**
 * Stateless UI for [TempNavigationScreen].
 *
 * @param showAdmin whether to show the Admin button.
 */
@Composable
internal fun TempNavigationContent(
    profileId: String,
    showAdmin: Boolean,
    onOwner: () -> Unit,
    onUser: () -> Unit,
    onAdmin: () -> Unit

){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Image(
            painter = painterResource(id = R.drawable.reboot_basecamp_logo),
            contentDescription = "Reboot Campsite Lottery logo",
            modifier = Modifier.size(200.dp),
            contentScale = ContentScale.Fit
        )

        Button(
            onClick = onUser,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
        ) {
            Text("Entrant", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onOwner,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                contentColor = MaterialTheme.colorScheme.inverseSurface
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
        ) {
            Text("Owner", fontWeight = FontWeight.Bold)
        }

        if (showAdmin) {
            Button(
                onClick = onAdmin,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    contentColor = MaterialTheme.colorScheme.inverseSurface
                ),
                contentPadding = PaddingValues(vertical = 16.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
            ) {
                Text("Admin", fontWeight = FontWeight.Bold)
            }
        }


        Spacer(Modifier.height(16.dp))
        SelectionContainer {
            Text(
                "Device ID: $profileId",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

    }
}

