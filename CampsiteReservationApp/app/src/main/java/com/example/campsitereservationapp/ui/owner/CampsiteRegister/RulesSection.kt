/**
 * This file contains separated UI component of Campsite Registration Screen
 * AI use:
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp.ui.owner.CampsiteRegister

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Campsite rules text and the checkbox that controls whether entrants must accept them.
 * When the owner unchecks it, entrants still see the rules but don't have to accept them to book.
 *
 * @param acceptanceRequired whether entrants must accept the rules before booking.
 * @param onAcceptanceRequiredChange event call when the owner checks or unchecks the box.
 */
@Composable
internal fun RulesSection(
    rules: String,
    onRulesChange: (String) -> Unit,
    acceptanceRequired: Boolean,
    onAcceptanceRequiredChange: (Boolean) -> Unit
) {
    Column {
        SectionHeader(
            title = "Rules & policies",
            subtitle = if (acceptanceRequired) "Entrants must accept these rules before confirming their reservation."
            else "Entrants can read these rules, but don't have to accept them."
        )
        Spacer(Modifier.height(12.dp))
        LabeledField(label = "Campsite rules") {
            OutlinedTextField(
                value = rules,
                onValueChange = onRulesChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAcceptanceRequiredChange(!acceptanceRequired) }
        ) {
            Checkbox(checked = acceptanceRequired, onCheckedChange = onAcceptanceRequiredChange)
            Text("Acceptance required before reservation confirmation", style = MaterialTheme.typography.bodySmall)
        }
    }
}
