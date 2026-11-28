/**
 * This file holds the User UserCurrentLotteriesScreen and any other small helper UI
 */
package com.example.campsitereservationapp.ui.entrant.CurrentLotteries

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import androidx.compose.material3.MaterialTheme

/**
 * Displays the current lotteries of the entrant
 *
 * Gives the user a list of either their ongoing lotteries or won lotteries
 * They can pick which using a button
 * Won lotteries can be claimed and ongoing lotteries can be left
 * 
 * @param [modifier] is optional modifier
 * @param [vm] is optional viewmodel
 * @param [profileId] is the document id of the entrant's profile
 * @param [onBack] is lambda for going back
 * @param [onToBooking] is lambda called to go to booking screen
 */
@Composable
fun UserCurrentLotteriesScreen(
    modifier: Modifier = Modifier,
    vm: UserCurrentLotteriesViewModel = viewModel(),
    profileId: String,
    onBack: () -> Unit,
    onToBooking: (lotteryId: String) -> Unit,
){
    vm.start(profileId)
    var showWon by remember{ mutableStateOf(true) }
    var showOngoing by remember{ mutableStateOf(true) }
    var allButtonToggled by remember { mutableStateOf(true) }
    var wonButtonToggled by remember { mutableStateOf(false) }
    var ongoingButtonToggled by remember { mutableStateOf(false) }

    //--------Back Button----------
    FilledTonalIconButton(
        onClick = onBack,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }


    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Spacer(Modifier.padding(32.dp))

        // Filter buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {


            //all button
            Button(
                onClick = {
                    showWon = true
                    showOngoing = true
                    allButtonToggled = true
                    wonButtonToggled = false
                    ongoingButtonToggled = false
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                colors = if (allButtonToggled) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            ) {
                Text(
                    "All",
                    fontWeight = FontWeight.Medium,
                )
            }
            //won button
            Button(
                onClick = {
                    showWon = true
                    showOngoing = false
                    allButtonToggled = false
                    wonButtonToggled = true
                    ongoingButtonToggled = false
                },
                colors = if (wonButtonToggled) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            ) {
                Text("Won", fontWeight = FontWeight.Medium)
            }
            // Ongoing button
            Button(
                onClick = {
                    showWon = false
                    showOngoing = true
                    allButtonToggled = false
                    wonButtonToggled = false
                    ongoingButtonToggled = true
                },
                colors = if (ongoingButtonToggled) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSurface,
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            ) {
                Text("Ongoing", fontWeight = FontWeight.Medium)
            }

        }

        if (showWon) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(vm.wonLotteries.toList()) { lottery ->
                    UserLotteryCard(
                        lottery = lottery,
                        imageURL = vm.getLotteryImage(lottery),
                        profileId = profileId,
                        buttonLabel = "Claim",
                        onClick = { wonLottery, _ -> onToBooking(wonLottery.id) }
                    )
                }
            }
        }
        if (showOngoing) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                items(vm.ongoingLotteries.toList()) { lottery ->
                    UserLotteryCard(
                        lottery = lottery,
                        imageURL = vm.getLotteryImage(lottery),
                        profileId = profileId,
                        buttonLabel = "Leave Lottery",
                        onClick = vm::leaveLottery
                    )
                }
            }
        }
    }
}