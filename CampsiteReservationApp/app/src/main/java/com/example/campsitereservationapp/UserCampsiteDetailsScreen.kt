package com.example.campsitereservationapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.apps.card.v1.Widget

@Composable
fun UserCampsiteDetailsScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Button(
            onClick = {/* Will take the user back to UserSearchCampsiteScreen*/},
            modifier = Modifier.testTag("backButton") // for testing
        ) {
            Text(" <- ")
        }
        Text(
            text = "Pine Valley Retreat",
            fontSize = 32.sp,
            fontWeight = FontWeight(800)
        ) // Should be the name of the selected listing

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "1284 Pine Valley Road, Yosemite, CA 95389",
            fontSize = 20.sp,
            maxLines = 1
        ) // Should be the address of the selected listing

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "4.9 (128 reviews)",
            fontSize = 20.sp
        ) // Should be the actual reviews and review count of the selected listing

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Maximum Occupancy:",
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "4 Campers",
                fontSize = 20.sp,
                fontWeight = FontWeight(650)
            ) // Should be the actual occupancy

        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = "Owner",
                fontSize = 20.sp,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Sarah J.",
                fontSize = 20.sp,
                fontWeight = FontWeight(650)
            ) // Should be the owner name

        }
    }
}
