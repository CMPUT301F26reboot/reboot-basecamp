package com.example.campsitereservationapp.ui.Admin

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.R
import com.example.campsitereservationapp.ui.Admin.HomeScreenComponents.HomeScreenBackButton
import com.example.campsitereservationapp.ui.Admin.HomeScreenComponents.HomeScreenButton
import com.example.campsitereservationapp.ui.Admin.HomeScreenComponents.HomeScreenLogo
import com.example.campsitereservationapp.ui.Admin.HomeScreenComponents.HomeScreenProfileButton
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent



@Composable
fun AdminHomeScreen(
    profileID: String,
    onBack: () -> Unit,
    onManageLotteries: () -> Unit,
    onManageProfiles: () -> Unit,
    onNotificationLogs: () -> Unit,
    onMessageLogs: () -> Unit,
    onUserImpersonate: () -> Unit,
    onOwnerImpersonate: () -> Unit,
    onProfile: () -> Unit
){

    HomeScreenBackButton(onBack = onBack)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HomeScreenLogo()

        HomeScreenButton(
            icon = Icons.Filled.Layers,
            text = "Manage Lotteries",
            onClick = onManageLotteries
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

        //===========drop down impersonation menu=====================
        var expanded by remember { mutableStateOf(false) }

        // Main button
        HomeScreenButton(
            text = "Impersonate",
            darkened = true,
            onClick = { expanded = !expanded }
        )

        // Expands when clicked to show 2 sub-buttons
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                HomeScreenButton(
                    icon = Icons.Filled.Person,
                    text = "User",
                    onClick = { onUserImpersonate }
                )
                HomeScreenButton(
                    icon = Icons.Filled.Home,
                    text = "Campsite Owner",
                    onClick = { onOwnerImpersonate }
                )
            }
        }
    }

    HomeScreenProfileButton( onProfileClick = onProfile)




}









