package com.example.mapmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mapmodel.ui.theme.MapModelTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MapModelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OtherScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

