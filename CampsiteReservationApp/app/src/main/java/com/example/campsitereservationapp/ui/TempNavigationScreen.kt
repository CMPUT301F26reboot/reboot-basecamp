package com.example.campsitereservationapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TempNavigationScreen(
    profileId: String,
    onOwner: () -> Unit,
    onUser: () -> Unit
){
    TempNavigationContent(
        profileId = profileId,
        onOwner = onOwner,
        onUser = onUser,
    )

}

@Composable
internal fun TempNavigationContent(
    profileId: String,
    onOwner: () -> Unit,
    onUser: () -> Unit

){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Button(onClick = onOwner, modifier = Modifier) {
            Text("Owner Home Screen")
        }

        Button(onClick = onUser, modifier = Modifier) {
            Text("User Home Screen")
        }
    }
}

