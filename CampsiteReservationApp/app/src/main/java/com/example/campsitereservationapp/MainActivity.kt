package com.example.campsitereservationapp

import CampsiteReservationApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.campsitereservationapp.ui.theme.CampsiteReservationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampsiteReservationAppTheme {
               /* UserWonSelectionScreen(
                    onBack = {},
                    onFinished = {},
                )*/

                CampsiteReservationApp()
            }
        }
    }
}


