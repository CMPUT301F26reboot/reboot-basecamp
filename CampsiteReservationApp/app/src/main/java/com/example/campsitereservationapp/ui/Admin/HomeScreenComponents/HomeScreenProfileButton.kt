package com.example.campsitereservationapp.ui.Admin.HomeScreenComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreenProfileButton(
    onProfileClick: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(26.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        FilledTonalIconButton(
            onClick = onProfileClick,
            modifier = Modifier.size(55.dp),
        ) {
            Icon(
                Icons.Filled.Person,
                contentDescription = "Profile",
                modifier = Modifier.size(35.dp)
            )
        }
    }

}
