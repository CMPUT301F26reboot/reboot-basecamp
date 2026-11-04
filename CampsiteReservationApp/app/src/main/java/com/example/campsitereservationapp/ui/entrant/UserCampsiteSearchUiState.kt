package com.example.campsitereservationapp.ui.entrant

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing

class UserCampsiteSearchUiState(
    var searchResults: SnapshotStateList<Listing> = mutableStateListOf<Listing>()
) {
}