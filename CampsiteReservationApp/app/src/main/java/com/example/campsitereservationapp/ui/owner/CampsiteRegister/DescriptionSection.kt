/*
 * AI use (full log: "AI Use Logs - Jeel/AI-Use-Log_Jeel.md"):
 * - Q: Where do I find what makes a box slightly rounded or more square?
 *   A: RoundedCornerShape(x.dp) from androidx.compose.foundation.shape. A smaller value is more
 *      square, and CircleShape is fully round.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp.ui.owner.CampsiteRegister

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Free-text campsite overview shown to entrants. */
@Composable
internal fun DescriptionSection(overview: String, onOverviewChange: (String) -> Unit) {
    Column {
        SectionHeader(title = "Description", subtitle = "Check-in guidance, amenities, fire regulations, etc.")
        Spacer(Modifier.height(12.dp))
        LabeledField(label = "Campsite Overview") {
            OutlinedTextField(
                value = overview,
                onValueChange = onOverviewChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
        }
    }
}
