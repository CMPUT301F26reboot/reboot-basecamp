/**
 * Admin Home Screen, main purpose it to navigate to other screens
 */
package com.example.campsitereservationapp.ui.Admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.R
import com.example.campsitereservationapp.ui.Admin.HomeScreenComponents.HomeScreenBackButton
import com.example.campsitereservationapp.ui.Admin.HomeScreenComponents.HomeScreenButton
import com.example.campsitereservationapp.ui.Admin.HomeScreenComponents.HomeScreenProfileButton

/**
 * The administrator's home screen: the logo, buttons for managing campsites and profiles and for
 * the notification and message logs, and a Role Switch button that expands to switch into the
 * entrant or owner side of the app.
 *
 * Outstanding issues:
 *  - Manage Campsites, Manage Profiles, both logs and the profile button have no screens yet.
 */
@Composable
fun AdminHomeScreen(
    onBack: () -> Unit,
    onManageCampsites: () -> Unit,
    onManageProfiles: () -> Unit,
    onNotificationLogs: () -> Unit,
    onMessageLogs: () -> Unit,
    onUserRoleSwitch: () -> Unit,
    onOwnerRoleSwitch: () -> Unit,
    onProfile: () -> Unit
){
    var showRoleSwitchOptions by remember { mutableStateOf(false) }

    HomeScreenBackButton(onBack = onBack)

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

        HomeScreenButton(
            icon = Icons.Filled.Layers,
            text = "Manage Campsites",
            onClick = onManageCampsites
        )
        HomeScreenButton(
            icon = Icons.Filled.Person,
            text = "Manage Profiles",
            onClick = onManageProfiles
        )
        HomeScreenButton(
            icon = Icons.Filled.Notifications,
            text = "Notification Logs",
            onClick = onNotificationLogs
        )
        HomeScreenButton(
            icon = Icons.Filled.Mail,
            text = "Message Logs",
            onClick = onMessageLogs
        )

        HomeScreenButton(
            text = "Role Switch",
            darkened = true,
            onClick = { showRoleSwitchOptions = !showRoleSwitchOptions }
        )

        AnimatedVisibility(visible = showRoleSwitchOptions) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                HomeScreenButton(
                    icon = Icons.Filled.Person,
                    text = "User",
                    onClick = onUserRoleSwitch
                )
                HomeScreenButton(
                    icon = Icons.Filled.Home,
                    text = "Campsite Owner",
                    onClick = onOwnerRoleSwitch
                )
            }
        }
    }

    HomeScreenProfileButton(onProfileClick = onProfile)
}
