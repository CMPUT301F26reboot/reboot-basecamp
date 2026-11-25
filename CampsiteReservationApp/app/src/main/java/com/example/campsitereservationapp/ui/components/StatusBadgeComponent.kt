package com.example.campsitereservationapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.campsitereservationapp.ui.theme.PillShape

/**
 * Reusable pill badge for a short status, such as "Active Draw", "Completed" or "Cancelled".
 * Pass colors from `MaterialTheme.colorScheme`, e.g. `secondaryContainer` with
 * `onSecondaryContainer`, so badges match across screens.
 *
 * @param text the status to show.
 * @param containerColor the badge's background.
 * @param contentColor the text color; a faint version of it is also used for the border.
 * @param modifier modifier from parent compose
 */
@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = PillShape,
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}