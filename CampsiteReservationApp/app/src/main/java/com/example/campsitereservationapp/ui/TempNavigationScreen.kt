package com.example.campsitereservationapp.ui

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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Queue
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    }
}

