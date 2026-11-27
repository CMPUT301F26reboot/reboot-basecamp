package com.example.campsitereservationapp.ui.Admin.BrowseProfile

import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel

/**
 * ViewModel for Browsing Profiles, basically a copy of AdminBrowseCampsiteViewModel
 * UserCampsiteSearchViewModel as for how search is done
 */
class AdminBrowseProfileViewModel(
): DatabaseModel() {

    //access viewmodel modified variables
    val _searchResults = mutableStateListOf<Profile>()

    /**
     * Searches for profiles based on name
     *
     * @param [keyword] string typed into search bar which is used to do a prefix query on the names of each campsite
     */
    fun profileSearch(keyword:String){
        _searchResults.clear()
        for (profile in _profiles){
            if(meetsKeywordRequirements(keyword, profile))
            {
                _searchResults.add(profile)
            }
        }
    }

    /**
     * Deletes profile
     * @param profile what Profile is being deleted
     */
    fun profileDelete(
        profile: Profile,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (profile.id.isBlank()) return

        profilesRef.document(profile.id).delete()
            .addOnSuccessListener {
                _searchResults.remove(profile)
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    /**
     * Deletes Profile Picture
     * @param profile what profile pfp belongs to
     */
    fun profilePictureDelete(
        profile: Profile,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        if (profile.id.isBlank() || profile.profilePicture.isBlank()) return

        profilesRef.document(profile.id)
            .update("profilePicture", "")
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }


    /**
     * Copied from UserCampsiteSearchViewModel
     * Shows if a listing meets the keyword requirements
     *
     * Compares the keyword with the listing name using compareTo
     */
    internal fun meetsKeywordRequirements(keyword: String, profile: Profile): Boolean{
        return keyword.isEmpty() || (keyword.compareTo(profile.name, ignoreCase = true) <= 0                 //keyword is a prefix or comes before
                && profile.name.compareTo(keyword + '\uffff', ignoreCase = true) < 0) //keyword + large character is larger
    }



}