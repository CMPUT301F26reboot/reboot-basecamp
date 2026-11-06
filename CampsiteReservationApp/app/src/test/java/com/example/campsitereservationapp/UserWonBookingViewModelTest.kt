/*
 * AI use:
 * - Q: How do I test ViewModels that connect to Firebase when they're created?
 *   A: Plain JUnit for the models; MockK to mock Firebase.firestore for the ViewModels. it gave me an option to fake the firebase also
 * - Some of the mock tests were written using Claude's output: the shared MockFirestore.kt helper
 *   and the ViewModel tests that use it.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.ui.entrant.WonBooking.UserWonBookingViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * Mock tests for [UserWonBookingViewModel], the controller behind the entrant's Won Booking screen
 * (US 01.04.01 view reservable dates, US 01.04.03 decline, US 01.05.01 book). Firestore is mocked,
 * and test data is pushed in through the same snapshot listeners the real database would use.
 */
class UserWonBookingViewModelTest {

    private val lotteryId = "lottery-jul-2027-banff"
    private val entrantId = "entrant-device-a81c4e07"

    private val twoJack = Listing(
        name = "Two Jack Lakeside",
        address = "Lake Minnewanka Scenic Dr, Banff, AB T1L 1K2",
        maxOccupancy = 6,
        rulesAcceptanceRequired = true
    ).apply { id = "listing-two-jack" }

    private val julyLottery = Lottery(
        dates = listOf(at(2027, 7, 9, 15), at(2027, 7, 10, 15), at(2027, 7, 11, 15)),
        campsiteIDs = listOf("listing-two-jack")
    ).apply { id = lotteryId }

    private val twoJackJuly10 = Reservation(
        dates = listOf(at(2027, 7, 10, 15)),
        entrantID = "entrant-device-0c9d55e1",
        campsiteID = "listing-two-jack"
    )

    private val otherCampsiteJuly9 = Reservation(
        dates = listOf(at(2027, 7, 9, 15)),
        entrantID = "entrant-device-5be2f310",
        campsiteID = "listing-tunnel-mtn"
    )

    private val listeners = mutableMapOf<String, EventListener<QuerySnapshot>>()
    private val collections = mutableMapOf<String, CollectionReference>()
    private lateinit var db: FirebaseFirestore
    private lateinit var viewModel: UserWonBookingViewModel

    /** A date at [hour]:00, like a Firestore timestamp. [month] is 1-based. */
    private fun at(year: Int, month: Int, day: Int, hour: Int = 0): Date {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month - 1, day, hour, 0)
        return cal.time
    }

    @Before
    fun setUp() {
        db = mockk(relaxed = true)
        listOf("Listings", "Lotteries", "Reservations").forEach { name ->
            val ref = mockk<CollectionReference>(relaxed = true)
            every { ref.addSnapshotListener(any<EventListener<QuerySnapshot>>()) } answers {
                listeners[name] = firstArg()
                mockk(relaxed = true)
            }
            every { db.collection(name) } returns ref
            collections[name] = ref
        }
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        every { Firebase.firestore } returns db

        viewModel = UserWonBookingViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /** Sends [items] to the ViewModel as if Firestore's [collection] listener had fired. */
    private fun <T : Any> deliver(collection: String, type: Class<T>, items: List<T>) {
        val docs = items.map { item -> mockk<DocumentSnapshot> { every { toObject(type) } returns item } }
        val snapshot = mockk<QuerySnapshot> { every { documents } returns docs }
        listeners.getValue(collection).onEvent(snapshot, null)
    }

    private fun openTwoJackOffer(reservations: List<Reservation> = listOf(twoJackJuly10, otherCampsiteJuly9)) {
        deliver("Listings", Listing::class.java, listOf(twoJack))
        deliver("Lotteries", Lottery::class.java, listOf(julyLottery))
        deliver("Reservations", Reservation::class.java, reservations)
        viewModel.start(lotteryId, "Two Jack Lakeside")
    }

    /** Makes the decline write return a task whose listeners the test can fire. */
    private fun stubDeclineWrite(): Pair<() -> OnSuccessListener<Void>, () -> OnFailureListener> {
        val doc = mockk<DocumentReference>()
        val task = mockk<Task<Void>>()
        var onSuccess: OnSuccessListener<Void>? = null
        var onFailure: OnFailureListener? = null
        every { collections.getValue("Lotteries").document(lotteryId) } returns doc
        every { doc.update("declinedProfileIds", any()) } returns task
        every { task.addOnSuccessListener(any<OnSuccessListener<Void>>()) } answers { onSuccess = firstArg(); task }
        every { task.addOnFailureListener(any<OnFailureListener>()) } answers { onFailure = firstArg(); task }
        return { onSuccess!! } to { onFailure!! }
    }

    @Test
    fun isLoading_shouldBeTrueUntilLotteryAndCampsiteArrive() {
        viewModel.start(lotteryId, "Two Jack Lakeside")
        assertTrue(viewModel.isLoading)

        openTwoJackOffer()

        assertFalse(viewModel.isLoading)
        assertEquals("Two Jack Lakeside", viewModel.campsite?.name)
    }

    @Test
    fun bookableDays_shouldExcludeDaysReservedAtThisCampsiteOnly() {
        openTwoJackOffer()

        // July 10 is taken at Two Jack; the July 9 booking is at a different campsite
        assertEquals(setOf(at(2027, 7, 9), at(2027, 7, 11)), viewModel.bookableDays)
    }

    @Test
    fun noDatesAvailable_lotteryWithoutThisCampsite_shouldBeTrue() {
        julyLottery.campsiteIDs = listOf("listing-tunnel-mtn")

        openTwoJackOffer()

        assertTrue(viewModel.bookableDays.isEmpty())
        assertTrue(viewModel.noDatesAvailable)
    }

    @Test
    fun toggleDay_shouldSelectBookableDayAndIgnoreReservedDay() {
        openTwoJackOffer()

        viewModel.toggleDay(at(2027, 7, 9))
        viewModel.toggleDay(at(2027, 7, 10))

        assertEquals(setOf(at(2027, 7, 9)), viewModel.selectedDays)
    }

    @Test
    fun canBook_shouldRequireDateAndRulesAcceptance() {
        openTwoJackOffer()
        viewModel.toggleDay(at(2027, 7, 9))
        assertFalse(viewModel.canBook) // Two Jack requires its rules to be accepted

        viewModel.onRulesAcceptedChanged(true)

        assertTrue(viewModel.canBook)
    }

    @Test
    fun declineOffer_writeSucceeds_shouldFinish() {
        val (onSuccess, _) = stubDeclineWrite()
        openTwoJackOffer()

        viewModel.declineOffer(entrantId)
        assertTrue(viewModel.isDeclining)
        onSuccess().onSuccess(null)

        assertFalse(viewModel.isDeclining)
        assertTrue(viewModel.isFinished)
    }

    @Test
    fun declineOffer_writeFails_shouldShowError() {
        val (_, onFailure) = stubDeclineWrite()
        openTwoJackOffer()

        viewModel.declineOffer(entrantId)
        onFailure().onFailure(Exception("Network unavailable"))

        assertFalse(viewModel.isFinished)
        assertEquals("Couldn't decline the offer. Please try again.", viewModel.errorMessage)
    }
}
