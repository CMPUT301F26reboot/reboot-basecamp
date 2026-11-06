package com.example.campsitereservationapp.ui.entrant.CurrentLotteries

import android.R
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery

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
    onToBooking: (lotteryId: String) -> Unit
){
    vm.start(profileId)
    var showWon by remember{ mutableStateOf(true) }
    var showOngoing by remember{ mutableStateOf(false) }

    //--------Back Button----------
    FilledTonalIconButton(
        onClick = onBack,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }


    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)){
        Spacer(Modifier.padding(32.dp))
        //--------Title-----------------

        Text(modifier = Modifier, fontSize = 28.sp, text = "Lotteries")
        //=============QUICKFIX FOR HEADER===========================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center


        ) {
            Button(
                onClick = { }, //need to navigate to lotteries screen
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    "Lotteries",
                    style = MaterialTheme.typography.titleMedium

                )
            }
            Spacer(Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { /* Already on this screen */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(
                        "Reservations",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                // the underline bar
                Box(
                    Modifier
                        .width(120.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }
        }
        //=======================================ENDOFQUICKFIX==========================

        //--------Which Lottery Type Buttons----
        Row(modifier = Modifier.fillMaxWidth(fraction = 0.7f), horizontalArrangement = Arrangement.spacedBy(10.dp)){
            //show won button
            TextButton(
                modifier = Modifier.toggleable(value = showWon, interactionSource = null, indication = null,
                    onValueChange = {}),
                shape = RoundedCornerShape(12.dp),
                onClick = {showWon = true
                    showOngoing = false},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Gray)
                )
            {
                Text("Show Won")
            }
            //show ongoing button
            TextButton(
                modifier = Modifier.toggleable(
                    value = showOngoing, interactionSource = null, indication = null,
                    onValueChange = {}),
                shape = RoundedCornerShape(12.dp),
                onClick = {showWon = false
                    showOngoing = true},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(1.dp, Color.Gray)
            )
            {
                Text("Show Ongoing")
            }
        }

        if(showWon){
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(vm.wonLotteries.toList()){ lottery ->
                    WonLotteryCard(
                        lottery = lottery,
                        imageURL = vm.getLotteryImage(lottery),
                        profileId = profileId,
                        onToBooking = onToBooking)
                }
            }
        }else{
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(vm.ongoingLotteries.toList()){ lottery ->
                    OngoingLotteryCard(
                        lottery = lottery,
                        imageURL = vm.getLotteryImage(lottery),
                        profileId = profileId,
                        onToLeave = vm::leaveLottery
                        )
                }
            }
        }
    }
}

/**
 * Card for a won lottery
 * 
 * Shows image, title, and a claim button which takes user to booking screen
 * 
 * @param [lottery] is the lottery being displayed
 * @param [imageURL] is the image for that lottery
 * @param [profileId] is document id of the entrant's profile.
 * Not used right now but will be passed on to the onToBooking lambda when it's finished
 * @param [onToBooking] lambda to go to booking screen
 */
@Composable
fun WonLotteryCard(
    lottery: Lottery,
    imageURL: String,
    profileId: String,
    onToBooking: (lotteryId: String) -> Unit
    ){
    OutlinedCard(modifier = Modifier.fillMaxWidth(fraction = 0.9f)){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image Box (occupies 25% width of row)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .height(95.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    //select first photo, show nothing if Null
                    val firstPhoto = imageURL
                    if (firstPhoto.isNotEmpty()) {
                        AsyncImage(
                            model = firstPhoto,
                            contentDescription = lottery.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF6B7280)
                        )
                    }
                }

                // Information details about campsite (occupies rest of space)
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = lottery.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )

                    Button(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        onClick = {onToBooking(lottery.id)},
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ){
                        Text("Claim")
                    }
                }
            }
        }
    }
}

/**
 * Card for an ongoing lottery
 * 
 * Shows image, title, and a claim button which takes user to booking screen
 * 
 * @param [lottery] is the lottery being displayed
 * @param [imageURL] is the image for that lottery
 * @param [profileId] is document id of the entrant's profile.
 * @param [onToLeave] lambda to go leave lottery (vm::leaveLottery)
 */
@Composable
fun OngoingLotteryCard(
    lottery: Lottery,
    imageURL: String,
    profileId: String,
    onToLeave: (Lottery, String) -> Unit
){
    OutlinedCard(modifier = Modifier.fillMaxWidth(fraction = 0.9f)){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Image Box (occupies 25% width of row)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.25f)
                        .height(95.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE5E7EB)),
                    contentAlignment = Alignment.Center
                ) {
                    //select first photo, show nothing if Null
                    val firstPhoto = imageURL
                    if (firstPhoto.isNotEmpty()) {
                        AsyncImage(
                            model = firstPhoto,
                            contentDescription = lottery.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = Color(0xFF6B7280)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = lottery.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(0.7f),
                        onClick = {onToLeave(lottery, profileId)},
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ){
                        Text("Leave")
                    }

                }
            }
        }
    }
}