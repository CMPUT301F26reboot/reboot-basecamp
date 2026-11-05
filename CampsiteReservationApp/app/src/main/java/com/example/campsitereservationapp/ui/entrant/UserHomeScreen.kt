package com.example.campsitereservationapp.ui.entrant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun UserHomeScreen(
    profileId: String,
    onSearchCampsites: () -> Unit,
    onMyLotteries: () -> Unit,
    onMyReservations: () -> Unit,
    onNotifications: () -> Unit,
    onProfile: () -> Unit,
){
    UserHomeContent(
        profileId = profileId,
        onSearchCampsites = onSearchCampsites,
        onMyLotteries = onMyLotteries,
        onMyReservations = onMyReservations,
        onNotifications = onNotifications,
        onProfile = onProfile
    )

}

@Composable
internal fun UserHomeContent(
    profileId: String,
    onSearchCampsites: () -> Unit,
    onMyLotteries: () -> Unit,
    onMyReservations: () -> Unit,
    onNotifications: () -> Unit,
    onProfile: () -> Unit,

){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onSearchCampsites, modifier = Modifier) {
            Text("Search Campsites")
        }

        Button(onClick = onMyLotteries, modifier = Modifier) {
            Text("My Lotteries")
        }

        Button(onClick = onMyReservations, modifier = Modifier) {
            Text("My Reservations")
        }

        Button(onClick = onNotifications, modifier = Modifier) {
            Text("Notifications")
        }

        Button(onClick = onProfile, modifier = Modifier) {
            Text("My Profile")
        }
    }
}