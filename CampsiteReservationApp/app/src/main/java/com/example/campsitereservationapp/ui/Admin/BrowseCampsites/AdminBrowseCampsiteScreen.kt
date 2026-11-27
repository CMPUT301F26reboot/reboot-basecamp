/**
 * This file holds the UI for Admin Browse Campsites,
 *
 *
 */
package com.example.campsitereservationapp.ui.Admin.BrowseCampsites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.CampsiteCard
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.SearchBarComponent
import com.example.campsitereservationapp.ui.components.BackButtonAndHeaderScreenComponent
import com.example.campsitereservationapp.ui.entrant.CampsiteSearch.UserSearchCampsiteCard
import java.util.Date

@Composable
/**
 * UI for Admin Campsite Search
 */
fun AdminBrowseCampsitesScreen(
    onBack:() -> Unit,
    onToDetails: (listingId: String, start: Date?, end: Date?) -> Unit,
    viewModel: AdminBrowseCampsiteViewModel = viewModel(),
){
    val listings = viewModel._searchResults


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BackButtonAndHeaderScreenComponent(
            title = "Browse Campsites",
            onBack = onBack
        )
        Spacer(Modifier.height(24.dp))
        SearchBarComponent(
            onSearch = { query ->
                viewModel.campsiteSearch(query)
            }
        )
        Spacer(Modifier.height(24.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listings, key = {it.id}) { listing ->
                CampsiteCard(
                    listing = listing,
                    onDelete = {}
                )
            }

        }




    }

}