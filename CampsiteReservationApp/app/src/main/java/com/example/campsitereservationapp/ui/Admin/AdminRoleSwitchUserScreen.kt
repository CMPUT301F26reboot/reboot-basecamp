package com.example.campsitereservationapp.ui.Admin

import androidx.compose.runtime.Composable
import com.example.campsitereservationapp.ui.Admin.ImpersonationScreenComponents.ImpersonationButton
import com.example.campsitereservationapp.ui.entrant.UserHomeScreen

/**
 * Screen used when an Admin impersonates an Entrant user.
 * Wraps [UserHomeScreen] and inserts the gold Exit Impersonation button.
 */
@Composable
fun AdminRoleSwitchUserScreen(
    profileId: String,
    onBack: () -> Unit = {},
    onSearchCampsites: () -> Unit = {},
    onMyLotteries: () -> Unit = {},
    onMyReservations: () -> Unit = {},
    onNotifications: () -> Unit = {},
    onProfile: () -> Unit = {},
    onExitImpersonation: () -> Unit = {}
) {
    UserHomeScreen(
        profileId = profileId,
        onBack = onBack,
        onSearchCampsites = onSearchCampsites,
        onMyLotteries = onMyLotteries,
        onMyReservations = onMyReservations,
        onNotifications = onNotifications,
        onProfile = onProfile,
        adminExitImpersonation = {
            ImpersonationButton(onClick = onExitImpersonation)
        }
    )
}

