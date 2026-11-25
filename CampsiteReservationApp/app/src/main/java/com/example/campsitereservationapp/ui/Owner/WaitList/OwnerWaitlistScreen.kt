package com.example.campsitereservationapp.ui.owner.Watilist

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.Owner.WaitList.LotteryInfoHeader
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent

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

    Column(
        modifier = modifier
    ){
        BackButtonAndHeaderScreenComponent(
            modifier = modifier,
            title = "Lottery Waitlist",
            subHeader = "View and notify people waiting for this draw",
            onBack = onBack
        )

        LotteryInfoHeader(
            title = lottery?.title,
            formatedDates = viewModel.getFormatedDates(lottery)
        )



    }



}