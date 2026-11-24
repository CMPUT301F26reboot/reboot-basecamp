/**
 * Gate for AdminAccessViewModel, shows a loading UI while checking, returns action if
 * admin check returns failure
 */
package com.example.campsitereservationapp.ui.Admin.AdminSetup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

/** What [AdminGate] shows: the loading spinner, the admin screen, or nothing (access denied). */
enum class AdminGateState { Loading, Allowed, Denied }

/**
 * Decides what [AdminGate] shows from the admin check. Kept separate from the composable so it can
 * be unit tested without a screen or Firestore.
 *
 * @param isChecking true while the `Admins` lookup is still running.
 * @param isAdmin true if the lookup found this device in `Admins`.
 * @return [AdminGateState.Loading] while checking, otherwise [AdminGateState.Allowed] for an admin
 *  and [AdminGateState.Denied] for anyone else.
 */
fun adminGateState(isChecking: Boolean, isAdmin: Boolean): AdminGateState = when {
    isChecking -> AdminGateState.Loading
    isAdmin -> AdminGateState.Allowed
    else -> AdminGateState.Denied
}

/**
 * Wraps an admin-only screen. Shows a spinner while [AdminAccessViewModel] checks the device, shows
 * [content] if the device is an admin, and calls [onDenied] otherwise. Used on every admin route so
 * the screens stay closed even if something navigates to them directly, not just through the
 * hidden Admin button.
 *
 * @param deviceId the ID of this device.
 * @param onDenied called once if the device is not an admin, usually to leave the screen.
 * @param content the admin screen.
 */
@Composable
fun AdminGate(
    deviceId: String,
    onDenied: () -> Unit,
    viewModel: AdminAccessViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    LaunchedEffect(deviceId) {
        viewModel.check(deviceId)
    }

    val state = adminGateState(viewModel.isChecking, viewModel.isAdmin)
    LaunchedEffect(state) {
        if (state == AdminGateState.Denied) onDenied()
    }

    when (state) {
        AdminGateState.Loading -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        AdminGateState.Allowed -> content()
        AdminGateState.Denied -> Unit
    }
}
