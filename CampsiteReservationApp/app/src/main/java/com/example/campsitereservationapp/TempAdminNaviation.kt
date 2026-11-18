package com.example.campsitereservationapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.campsitereservationapp.ui.Admin.AdminHomeScreen
import com.example.campsitereservationapp.ui.Admin.AdminImpersonationOwnerScreen
import com.example.campsitereservationapp.ui.Admin.AdminImpersonationUserScreen
import com.example.campsitereservationapp.utils.getDeviceId

@Composable
fun TempAdminNavigation() {
    val navController = rememberNavController()

    // Get the user's deviceID
    val context = LocalContext.current
    val deviceId = remember { getDeviceId(context) }

    NavHost(
        navController = navController,
        startDestination = "admin_home"
    ) {
        // 1. Main Admin Home Screen
        composable(route = "admin_home") {
            AdminHomeScreen(
                profileID = deviceId,
                onBack = { navController.popBackStack() },
                onManageCampsites = {},
                onManageProfiles = {},
                onNotificationLogs = {},
                onMessageLogs = {},
                onUserImpersonate = { navController.navigate("admin_impersonate_user") },
                onOwnerImpersonate = { navController.navigate("admin_impersonate_owner") },
                onProfile = {}
            )
        }

        // 2. Impersonate Entrant Screen
        composable(route = "admin_impersonate_user") {
            AdminImpersonationUserScreen(
                profileId = deviceId,
                onBack = { navController.popBackStack() },
                onExitImpersonation = {
                    // Returns directly back to Admin Home screen
                    navController.popBackStack("admin_home", false)
                }
            )
        }

        // 3. Impersonate Owner Screen
        composable(route = "admin_impersonate_owner") {
            AdminImpersonationOwnerScreen(
                profileId = deviceId,
                onBack = { navController.popBackStack() },
                onExitImpersonation = {
                    // Returns directly back to Admin Home screen
                    navController.popBackStack("admin_home", false)
                }
            )
        }
    }
}