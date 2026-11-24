/**
 * Simply shows a "loading" UI when checking if a device is an admin
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

    val denied = !viewModel.isChecking && !viewModel.isAdmin
    LaunchedEffect(denied) {
        if (denied) onDenied()
    }

    when {
        viewModel.isChecking -> Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        viewModel.isAdmin -> content()
    }
}
