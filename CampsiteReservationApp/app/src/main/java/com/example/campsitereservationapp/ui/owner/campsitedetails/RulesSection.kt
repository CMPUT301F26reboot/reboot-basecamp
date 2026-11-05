package com.example.campsitereservationapp.ui.owner.campsitedetails

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

/** Campsite rules text and the checkbox that controls whether entrants must accept them. */
@Composable
internal fun RulesSection(
    rules: String,
    onRulesChange: (String) -> Unit,
    acceptanceRequired: Boolean,
    onAcceptanceRequiredChange: (Boolean) -> Unit
) {
    Column {
        SectionHeader(title = "Rules & policies", subtitle = "Entrants must accept these rules before confirming their reservation.")
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = true, onCheckedChange = {}, enabled = false)
            Text("Acceptance required before reservation confirmation", style = MaterialTheme.typography.bodySmall)
        }
    }
}
