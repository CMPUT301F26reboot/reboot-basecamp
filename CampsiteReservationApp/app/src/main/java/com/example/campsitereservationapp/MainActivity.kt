/** This file contains the application creation and runs the app navigation CampsiteReservationApp*/

package com.example.campsitereservationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.campsitereservationapp.ui.MessageScreen
import com.example.campsitereservationapp.ui.owner.WaitList.OwnerWaitlistScreen
import com.example.campsitereservationapp.ui.theme.CampsiteReservationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampsiteReservationTheme {
                CampsiteReservationApp()
                //MessageScreen(  "V1hnZFPqEJQ4B2xCdEHM","W8liJRiysiUFhsZHVpKs",{})
                //ui.Owner.CampsiteDetails.dateViewer()
            }
        }
    }
}