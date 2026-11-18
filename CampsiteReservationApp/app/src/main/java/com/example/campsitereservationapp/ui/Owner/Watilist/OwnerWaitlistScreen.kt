package com.example.campsitereservationapp.ui.Owner.Watilist

import android.app.Notification
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.owner.OwnerLotteryCreationViewModel

@Composable
fun OwnerWaitlistScreen(
    modifier: Modifier,
    lotteryID: String,
    onBack: () -> Unit = {},
    onSendNotification: () -> Unit = {},
    viewModel: OwnerWaitlistViewModel = viewModel()

){
    //get needed information
    val lottery = viewModel.getLottery(lotteryID)
    val formatedLotteryDates = viewModel.getFormatedDates(lottery)
    val waitlist = lottery?.waitlist ?: emptyList()



}