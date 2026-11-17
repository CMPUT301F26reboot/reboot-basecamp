package com.example.campsitereservationapp.ui.Admin.HomeScreenComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.R

/**
 * Reusable Component for homescreen logo
 */
@Composable
fun HomeScreenLogo(){
    Image(
        painter = painterResource(id = R.drawable.reboot_basecamp_logo),
        contentDescription = "Reboot Campsite Lottery logo",
        modifier = Modifier.size(200.dp),
        contentScale = ContentScale.Fit
    )

}

@Preview(showBackground = true)
@Composable
fun HomeScreenLogoPreview() {
    HomeScreenLogo()
}