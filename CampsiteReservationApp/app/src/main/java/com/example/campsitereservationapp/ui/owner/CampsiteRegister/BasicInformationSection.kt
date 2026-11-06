package com.example.campsitereservationapp.ui.owner.CampsiteRegister

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/** Site name, location, and max occupancy stepper. */
@Composable
internal fun BasicInformationSection(
    siteName: String,
    onSiteNameChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    maxOccupancy: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Column {
        SectionHeader(title = "Basic Information", subtitle = "Essential details displayed to lottery entrants")
        Spacer(Modifier.height(12.dp))

        LabeledField(label = "Site Name") {
            OutlinedTextField(
                value = siteName,
                onValueChange = onSiteNameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        Spacer(Modifier.height(16.dp))

        LabeledField(label = "Address") {
            OutlinedTextField(
                value = location,
                onValueChange = onLocationChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) }
            )
        }
        Spacer(Modifier.height(16.dp))

        LabeledField(label = "Max Occupancy") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepperButton(symbol = "−", contentDescription = "Decrease occupancy", onClick = onDecrement)
                Text("$maxOccupancy Guests", style = MaterialTheme.typography.bodyLarge)
                StepperButton(symbol = "+", contentDescription = "Increase occupancy", onClick = onIncrement)
            }
        }
    }
}

/** Round +/- button used by the occupancy stepper. [symbol] is drawn as text since +/- glyphs render reliably without needing extra icon assets. */
@Composable
private fun StepperButton(symbol: String, contentDescription: String, onClick: () -> Unit) {
    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceVariant) {
        IconButton(onClick = onClick) {
            Text(symbol, style = MaterialTheme.typography.titleMedium, modifier = Modifier.semantics { this.contentDescription = contentDescription })
        }
    }
}
