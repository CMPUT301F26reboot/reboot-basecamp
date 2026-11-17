package com.example.campsitereservationapp.ui.Admin

import android.graphics.drawable.Icon
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
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.R

@Composable
fun AdminHomeScreen(
    profileID: String,
    onManageLotteries: () -> Unit,
    onManageProfiles: () -> Unit,
    onNotificationLogs: () -> Unit,
    onMessageLogs: () -> Unit,
    onUserImpersonate: () -> Unit,
    onOwnerImpersonate: () -> Unit,
){
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
            onClick = onManageLotteries
        )
        HomeScreenButton(
            icon = Icons.Filled.Notifications,
            text = "Notification Logs",
            onClick = onManageLotteries
        )
        HomeScreenButton(
            icon = Icons.Filled.Mail,
            text = "Message Logs",
            onClick = onManageLotteries
        )

    }


}



@Composable
fun HomeScreenButton(
    icon: ImageVector,
    text: String,
    darkened: Boolean = false,
    onClick: () -> Unit,

){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = if (darkened) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                contentColor = MaterialTheme.colorScheme.inverseSurface
            )
        },
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.Bold)
    }

}

@Composable
fun HomeScreenLogo(){
    Image(
        painter = painterResource(id = R.drawable.reboot_basecamp_logo),
        contentDescription = "Reboot Campsite Lottery logo",
        modifier = Modifier.size(200.dp),
        contentScale = ContentScale.Fit
    )

}



