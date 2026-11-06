package com.example.campsitereservationapp.ui.owner

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
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

@Composable
fun OwnerHomeScreen(
    profileId: String,
    onBack: () -> Unit,
    onManageLotteries: () -> Unit,
    onManageCampsites: () -> Unit,
    onNotificationHistory: () -> Unit,
    onProfile: () -> Unit,
){
    OwnerHomeContent(
        profileId = profileId,
        onBack = onBack,
        onManageLotteries = onManageLotteries,
        onManageCampsites = onManageCampsites,
        onNotificationHistory = onNotificationHistory,
        onProfile = onProfile
    )

}

@Composable
internal fun OwnerHomeContent(
    profileId: String,
    onBack: () -> Unit,
    onManageLotteries: () -> Unit,
    onManageCampsites: () -> Unit,
    onNotificationHistory: () -> Unit,
    onProfile: () -> Unit,

    ){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FilledTonalIconButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
    }


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
            onClick = onManageLotteries,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
        ) {
            Icon(Icons.Filled.Queue, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Manage Lotteries", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onManageCampsites,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                contentColor = MaterialTheme.colorScheme.inverseSurface
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
        ) {
            Icon(Icons.Filled.List, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Manage Campsite Listings", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onNotificationHistory,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                contentColor = MaterialTheme.colorScheme.inverseSurface
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
        ) {
            Icon(Icons.Filled.Notifications, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Notification History", fontWeight = FontWeight.Bold)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        FilledTonalIconButton(
            onClick = onProfile,
            modifier = Modifier.size(55.dp),
        ) {
            Icon(
                Icons.Filled.Person,
                contentDescription = "Back",
                modifier = Modifier.size(35.dp)
            )
        }
    }
}