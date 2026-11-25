/**
 * Test for database model, adds a collection of every type to the database,
 * then retrieves them, makes sure what was added is the same as what it got back
 *
 * AI Refrence: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class DatabaseModelTest {

    /** Exposes DatabaseModel's protected local copies so the test can read them. */
    private class TestDatabaseModel : DatabaseModel() {
        val listings: List<Listing> get() = _listings
        val lotteries: List<Lottery> get() = _lotteries
        val profiles: List<Profile> get() = _profiles
        val logEntries: List<LogEntry> get() = _logentries
        val chats: List<Chat> get() = _chats
        val messages: List<Message> get() = _messages
    }

    private val firestore = MockFirestore()
    private lateinit var model: TestDatabaseModel

    @Before
    fun setUp() {
        firestore.install()
        model = TestDatabaseModel()
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun listings_addedListingIsRetrieved() {
        val listing = Listing(
            name = "Two Jack Lakeside",
            address = "Lake Minnewanka Scenic Dr, Banff, AB T1L 1K2",
            maxOccupancy = 6,
            ownerID = "owner-device-7f3a91c2",
            geoLocation = GeoPoint(51.2290, -115.5040)
        ).apply { id = "listing-two-jack" }
        val saved = slot<Listing>()

        // add: DatabaseModel sends the listing to the Listings collection
        model.addListing(listing)
        verify { firestore.collection("Listings").add(capture(saved)) }
        assertSame(listing, saved.captured)

        // retrieve: Firestore sends the saved listing back through the listener
        firestore.deliver("Listings", Listing::class.java, listOf(saved.captured))
        assertEquals(listOf(listing), model.listings)
    }

    @Test
    fun lotteries_addedLotteryIsRetrieved() {
        val lottery = Lottery(
            dates = listOf(day(2027, 7, 9), day(2027, 7, 10)),
            start = day(2027, 5, 1),
            end = day(2027, 5, 31),
            campsiteIDs = listOf("listing-two-jack")
        ).apply {
            id = "lottery-july"
            title = "July Long Weekend - Banff"
        }
        val saved = slot<Lottery>()

        model.addLottery(lottery)
        verify { firestore.collection("Lotteries").add(capture(saved)) }
        assertSame(lottery, saved.captured)

        firestore.deliver("Lotteries", Lottery::class.java, listOf(saved.captured))
        assertEquals(listOf(lottery), model.lotteries)
    }

    @Test
    fun profiles_deliveredProfilesAreRetrieved() {
        val profile = Profile(name = "Alex Chen", deviceId = "device-7f3a91c2").apply {
            id = "device-7f3a91c2"
            email = "alex@example.com"
            role = "Entrant"
        }

        firestore.deliver("Profiles", Profile::class.java, listOf(profile))

        assertEquals(listOf(profile), model.profiles)
    }

    @Test
    fun reservations_deliveredReservationsAreRetrieved() {
        val reservation = Reservation(
            dates = listOf(day(2027, 7, 9)),
            entrantID = "device-7f3a91c2",
            campsiteID = "listing-two-jack"
        ).apply { id = "reservation-1" }

        firestore.deliver("Reservations", Reservation::class.java, listOf(reservation))

        assertEquals(listOf(reservation), model.reservations)
    }

    @Test
    fun logEntries_deliveredLogEntriesAreRetrieved() {
        val logEntry = LogEntry()

        firestore.deliver("LogEntries", LogEntry::class.java, listOf(logEntry))

        assertEquals(listOf(logEntry), model.logEntries)
    }

    @Test
    fun chats_deliveredChatsAreRetrieved() {
        val chat = Chat(ownerID = "owner-device-7f3a91c2", entrantID = "device-7f3a91c2").apply {
            id = "chat-1"
            messageIDList = listOf("message-1")
        }

        firestore.deliver("Chats", Chat::class.java, listOf(chat))

        assertEquals(listOf(chat), model.chats)
    }

    @Test
    fun messages_deliveredMessagesAreRetrieved() {
        val message = Message(
            text = "Check-in is after 2 PM.",
            senderID = "owner-device-7f3a91c2",
            receiverID = "device-7f3a91c2",
            timestamp = Timestamp(1_800_000_000, 0)
        ).apply { id = "message-1" }

        firestore.deliver("Messages", Message::class.java, listOf(message))

        assertEquals(listOf(message), model.messages)
    }
}