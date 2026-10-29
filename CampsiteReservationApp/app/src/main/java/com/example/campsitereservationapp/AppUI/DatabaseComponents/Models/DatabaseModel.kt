package com.example.campsitereservationapp.AppUI.DatabaseComponents.Models

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.LogEntry
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlin.math.log

open class DatabaseModel :ViewModel() {
    protected var db = Firebase.firestore

    //Every collection
    protected var listingsRef = db.collection("Listings")
    protected var lotteriesRef = db.collection("Lotteries")
    protected var profilesRef = db.collection("Profiles")
    protected var reservationsRef = db.collection("Reservations")
    protected var logentriesRef = db.collection("LogEntries")
    protected var chatsRef = db.collection("Chats")

    //Every local copy of each collection
    protected var _listings = ArrayList<Listing>()
    protected var _lotteries = ArrayList<Lottery>()
    protected var _profiles = ArrayList<Profile>()
    protected var _reservations = ArrayList<Reservation>()
    protected var _logentries = ArrayList<LogEntry>()
    protected var _chats = ArrayList<Chat>()


    open val listings: List<Listing> get() = _listings //added

    //Adding a listener to each collection to update local copies
    init{
        listingsRef.addSnapshotListener{ snapshot, error ->
            if(error != null){
                return@addSnapshotListener
            }

            _listings.clear()
            snapshot?.documents?.forEach{document ->
                val listing = document.toObject(Listing::class.java)
                if(listing != null){
                    _listings.add(listing)
                }
            }
        }

        lotteriesRef.addSnapshotListener{ snapshot, error ->
            if(error != null){
                return@addSnapshotListener
            }

            _lotteries.clear()
            snapshot?.documents?.forEach{document ->
                val lottery = document.toObject(Lottery::class.java)
                if(lottery != null){
                    _lotteries.add(lottery)
                }
            }
        }

        profilesRef.addSnapshotListener{ snapshot, error ->
            if(error != null){
                return@addSnapshotListener
            }

            _profiles.clear()
            snapshot?.documents?.forEach{document ->
                val profile = document.toObject(Profile::class.java)
                if(profile != null){
                    _profiles.add(profile)
                }
            }
        }

        reservationsRef.addSnapshotListener{ snapshot, error ->
            if(error != null){
                return@addSnapshotListener
            }

            _reservations.clear()
            snapshot?.documents?.forEach{document ->
                val reservation = document.toObject(Reservation::class.java)
                if(reservation != null){
                    _reservations.add(reservation)
                }
            }
        }

        logentriesRef.addSnapshotListener{ snapshot, error ->
            if(error != null){
                return@addSnapshotListener
            }

            _logentries.clear()
            snapshot?.documents?.forEach{document ->
                val logentry = document.toObject(LogEntry::class.java)
                if(logentry != null){
                    _logentries.add(logentry)
                }
            }
        }

        chatsRef.addSnapshotListener{ snapshot, error ->
            if(error != null){
                return@addSnapshotListener
            }

            _chats.clear()
            snapshot?.documents?.forEach{document ->
                val chat = document.toObject(Chat::class.java)
                if(chat != null){
                    _chats.add(chat)
                }
            }
        }
    }

    //add delete methods
    fun addListing(listing: Listing){
        listingsRef.document(listing.name).set(listing)
    }




}