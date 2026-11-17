package com.example.campsitereservationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.campsitereservationapp.ui.Admin.AdminHomeScreen
import com.example.campsitereservationapp.ui.entrant.UserHomeScreen
import com.example.campsitereservationapp.ui.theme.CampsiteReservationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampsiteReservationAppTheme {
                AdminHomeScreen(
                    profileID = "1",
                    onManageLotteries = {},
                    onManageProfiles = {},
                    onNotificationLogs = {},
                    onMessageLogs = {},
                    onUserImpersonate = {},
                    onOwnerImpersonate = {}
                )


            }
        }
    }
}


