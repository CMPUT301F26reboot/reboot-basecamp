package com.example.campsitereservationapp.ui.owner.campsitedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun OwnerHomeScreen(
    profileId: String,
    onManageLotteries: () -> Unit,
    onMyCampsites: () -> Unit,
    onAddCampsite: () -> Unit,
    onNotificationHistory: () -> Unit,
    onProfile: () -> Unit,
){
    OwnerHomeContent(
        profileId = profileId,
        onManageLotteries = onManageLotteries,
        onMyCampsites = onMyCampsites,
        onAddCampsite = onAddCampsite,
        onNotificationHistory = onNotificationHistory,
        onProfile = onProfile
    )

}

@Composable
internal fun OwnerHomeContent(
    profileId: String,
    onManageLotteries: () -> Unit,
    onMyCampsites: () -> Unit,
    onAddCampsite: () -> Unit,
    onNotificationHistory: () -> Unit,
    onProfile: () -> Unit,

    ){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onManageLotteries, modifier = Modifier) {
            Text("Manage Lotteries")
        }

        Button(onClick = onMyCampsites, modifier = Modifier) {
            Text("My Campsite Listings")
        }

        Button(onClick = onAddCampsite, modifier = Modifier) {
            Text("Add New Campsite")
        }

        Button(onClick = onNotificationHistory, modifier = Modifier) {
            Text("Notification History")
        }

        Button(onClick = onProfile, modifier = Modifier) {
            Text("My Profile")
        }
    }
}