package com.example.campsitereservationapp.ui.Admin.BrowseProfile

import androidx.compose.runtime.mutableStateListOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.example.campsitereservationapp.ui.Admin.BrowseComponents.notFinished
import com.google.firebase.firestore.FieldValue
import java.util.Date

/**
 * ViewModel for Browsing Profiles, basically a copy of AdminBrowseCampsiteViewModel
 * UserCampsiteSearchViewModel as for how search is done
 */
class AdminBrowseProfileViewModel(
): DatabaseModel() {

    //access viewmodel modified variables
    val _searchResults = mutableStateListOf<Profile>()

    /** The last search, so the list can be refreshed with it when profiles load or change. */
    private var lastKeyword = ""

    /** How many profiles are loaded; the screen re-runs the search when this changes. */
    val profileCount: Int
        get() = _profiles.size

    /** Runs the last search again, e.g. once profiles have loaded from Firestore. */
    fun refreshSearch() {
        profileSearch(lastKeyword)
    }

    /**
     * Searches for profiles based on name
     *
     * @param [keyword] string typed into search bar which is used to do a prefix query on the names of each campsite
     */
    fun profileSearch(keyword:String){
        lastKeyword = keyword
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
                removeFromWaitlists(profile.id)
                cancelReservations(profile.id)
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    /**
     * Takes a deleted profile off every lottery waitlist, like a user deleting their own profile
     * does, so they can't be drawn as a winner.
     *
     * @param profileId the deleted profile.
     */
    /**
     * Cancels every reservation of a deleted profile that hasn't finished, upcoming or happening
     * now, as if the account never existed. Finished stays are left as the owner's history.
     * No notification is sent, since the profile no longer exists.
     *
     * @param profileId the deleted profile.
     * @param now the current time.
     */
    internal fun cancelReservations(profileId: String, now: Date = Date()) {
        _reservations
            .filter { it.entrantID == profileId && notFinished(it, now) }
            .forEach { reservation ->
                reservationsRef.document(reservation.id).update("status", "cancelled")
            }
    }

    private fun removeFromWaitlists(profileId: String) {
        _lotteries.filter { profileId in it.waitlist }.forEach { lottery ->
            lotteriesRef.document(lottery.id).update("waitlist", FieldValue.arrayRemove(profileId))
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
                // The search results keep their own copy of the profile, so clear the picture
                // there too and replace the item, so the card stops showing the old photo.
                profile.profilePicture = ""
                val position = _searchResults.indexOfFirst { it.id == profile.id }
                if (position >= 0) {
                    _searchResults.removeAt(position)
                    _searchResults.add(position, profile)
                }
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