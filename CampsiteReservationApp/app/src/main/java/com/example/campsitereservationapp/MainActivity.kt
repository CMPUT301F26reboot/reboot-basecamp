package com.example.campsitereservationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.example.campsitereservationapp.ui.owner.Watilist.OwnerWaitlistScreen
import com.example.campsitereservationapp.ui.theme.CampsiteReservationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampsiteReservationTheme {
                OwnerWaitlistScreen(
                    modifier = Modifier,
                    lotteryID = "2s1gAjo01zp2HBOEKJa0",
                    onBack = { },
                    onSendNotification = {},
                )
            }
        }
    }
}


