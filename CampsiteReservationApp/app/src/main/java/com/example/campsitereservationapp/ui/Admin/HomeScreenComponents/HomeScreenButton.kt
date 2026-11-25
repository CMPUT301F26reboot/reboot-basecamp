/**
 * UI Component of HomeScreen -> Main Buttons
 */
package com.example.campsitereservationapp.ui.Admin.HomeScreenComponents

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp


/**
 * Reusable Component for the homescreen button
 * @param icon [ImageVector] icon shown on left of text
 * @param text [String] text displayed on button
 * @param darkened [boolean] OPTIONAL, sets button to dark
 * @param onClick onclick event, navigates to corrosponding screen
 */
@Composable
fun HomeScreenButton(
    icon: ImageVector? = null,
    text: String,
    darkened: Boolean = false,
    onClick: () -> Unit,

    ){
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = if (darkened) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                contentColor = MaterialTheme.colorScheme.inverseSurface
            )
        },
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 32.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.Bold)
    }

}
