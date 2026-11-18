package com.example.campsitereservationapp.ui.owner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TempOwnerLotteryListScreen(
    profileId: String,
    onBack: () -> Unit,
    onCreateLottery: () -> Unit,
    onViewLotteries: () -> Unit
){
    TempOwnerLotteryListContent(
        profileId = profileId,
        onBack = onBack,
        onCreateLottery = onCreateLottery,
        onViewLotteries = onViewLotteries
    )

}

@Composable
internal fun TempOwnerLotteryListContent(
    profileId: String,
    onBack: () -> Unit,
    onCreateLottery: () -> Unit,
    onViewLotteries: () -> Unit

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

        Button(
            onClick = onCreateLottery,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
        ) {
            Text("Create Lottery", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = onViewLotteries,
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            contentPadding = PaddingValues(vertical = 16.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
        ) {
            Text("View Lotteries", fontWeight = FontWeight.Bold)
        }
    }
}