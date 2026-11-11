/*
 * AI use:
 * - Q: Where do I find what makes a box slightly rounded or more square?
 *   A: RoundedCornerShape(x.dp) from androidx.compose.foundation.shape. A smaller value is more
 *      square, and CircleShape is fully round.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp.ui.owner.CampsiteRegister

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.ui.theme.PillShape

/** Full-width black pill button that submits the form. */
@Composable
internal fun PublishButton(enabled: Boolean, isPublishing: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = PillShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onSurface,
            contentColor = MaterialTheme.colorScheme.surface
        ),
        contentPadding = PaddingValues(vertical = 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Filled.Add, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(if (isPublishing) "Publishing..." else "Publish Listing", fontWeight = FontWeight.Bold)
    }
}
