package com.example.campsitereservationapp.ui.owner.campsitedetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Tag input field plus the chips for tags already added. */
@Composable
internal fun TagsSection(tags: List<String>, onAddTag: (String) -> Unit, onRemoveTag: (String) -> Unit) {
    var draft by remember { mutableStateOf("") }

    Column {
        SectionHeader(title = "Tags", subtitle = "Add tags to help guests quickly find this campsite")
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = draft,
                onValueChange = { draft = it },
                placeholder = { Text("Add tag...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(onClick = {
                onAddTag(draft)
                draft = ""
            }) {
                Text("Add tag", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(4.dp))

        if (tags.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    TagChip(tag = tag, onRemove = { onRemoveTag(tag) })
                }
            }
        }
    }
}

/** Removable chip for one tag. */
@Composable
private fun TagChip(tag: String, onRemove: () -> Unit) {
    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 6.dp, bottom = 6.dp)) {
            Text(tag, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
            IconButton(onClick = onRemove, modifier = Modifier.size(20.dp)) {
                Icon(Icons.Filled.Close, contentDescription = "Remove tag $tag", modifier = Modifier.size(14.dp))
            }
        }
    }
}
