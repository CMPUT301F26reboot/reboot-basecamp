package com.example.campsitereservationapp.ui.entrant

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.UserReservationsViewModel


/**
 * User Reservations screen - US 01.06.01 (view active), US 01.06.02 (view past + rate),
 * US 01.07.01 (cancel active).
 */
@Composable
fun UserReservationsScreen(
    profileId: String,
    onBack: () -> Unit,
    onOpenReservation: (reservationId: String) -> Unit = {},
    viewModel: UserReservationsViewModel = viewModel()
){

}
