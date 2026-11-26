/**
 * This file contains the resuable search bar used in Admin Browse Campsite/Profile
 *
 */
package com.example.campsitereservationapp.ui.Admin.BrowseComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBarComponent(
    onSearch: (query: String) -> Unit
){
    var keyword by remember{mutableStateOf("")}
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(fraction = 0.7f).padding(5.dp),
            value = keyword,
            onValueChange = { keyword = it },
            label = { Text("Search Bar") },
            shape = MaterialTheme.shapes.medium,
        )

        Button(
            modifier = Modifier.fillMaxWidth().padding(5.dp),
            onClick = { onSearch(keyword)},
            shape = MaterialTheme.shapes.medium,
            contentPadding = PaddingValues(vertical = 14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSurface,
                contentColor = MaterialTheme.colorScheme.surface
            )
        ){
            Text("Search")
        }
    }
}