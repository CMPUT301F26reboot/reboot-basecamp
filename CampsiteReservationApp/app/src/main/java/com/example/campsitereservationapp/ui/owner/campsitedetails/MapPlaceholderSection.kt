package com.example.campsitereservationapp.ui.owner.campsitedetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Static placeholder for "drop a pin" location picking. No Maps/Places SDK or API key is
 * configured yet, so the search field, zoom controls, and "Update Location" button are all
 * disabled and purely cosmetic.
 */
@Composable
internal fun MapPlaceholderSection() {
    Column {
        SectionHeader(title = "Set Location on Map", subtitle = "Drop a pin to mark your campsite's exact location")
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            enabled = false,
            placeholder = { Text("Search address or place name...") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Icon(
                Icons.Filled.LocationOn,
                contentDescription = "Pin placeholder",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.Center).size(36.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface) {
                    Text("+", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
                }
                Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface) {
                    Text("−", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(onClick = {}, enabled = false) {
                Text("Update Location")
            }
        }
    }
}
