package com.example.campsitereservationapp.ui.Admin

import androidx.compose.runtime.Composable
import com.example.campsitereservationapp.ui.Admin.ImpersonationScreenComponents.ImpersonationButton
import com.example.campsitereservationapp.ui.owner.OwnerHomeScreen

/**
 * Screen used when an Admin impersonates an Owner user.
 * Wraps [OwnerHomeScreen] and inserts the gold Exit Impersonation button.
 */
@Composable
fun AdminImpersonationOwnerScreen(
    profileId: String,
    onBack: () -> Unit = {},
    onManageLotteries: () -> Unit = {},
    onManageCampsites: () -> Unit = {},
    onNotificationHistory: () -> Unit = {},
    onProfile: () -> Unit = {},
    onExitImpersonation: () -> Unit = {}
) {
    OwnerHomeScreen(
        profileId = profileId,
        onBack = onBack,
        onManageLotteries = onManageLotteries,
        onManageCampsites = onManageCampsites,
        onNotificationHistory = onNotificationHistory,
        onProfile = onProfile,
        adminExitImpersonation = {
            ImpersonationButton(onClick = onExitImpersonation)
        }
    )
}