/*
 * AI use:
 * - Q: How do I test ViewModels that connect to Firebase when they're created?
 *   A: Plain JUnit for the models; MockK to mock Firebase.firestore for the ViewModels.
 * - Some of the mock tests were written using Claude's output: the shared MockFirestore.kt helper
 *   and the ViewModel tests that use it.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.owner.OwnerLotteryCreationViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Mock tests for [OwnerLotteryCreationViewModel], which lets an owner create a lottery. */
class OwnerLotteryCreationViewModelTest {

    private val ownerId = "owner-device-7f3a91c2"

    private val twoJack = Listing(name = "Two Jack Lakeside", ownerID = ownerId).apply { id = "listing-two-jack" }
    private val tunnelMountain = Listing(name = "Tunnel Mountain Village II", ownerID = ownerId).apply { id = "listing-tunnel-mtn" }
    private val lakeLouise = Listing(name = "Lake Louise Hard-Sided", ownerID = "owner-device-22be0d14").apply { id = "listing-lake-louise" }

    private val firestore = MockFirestore()
    private val write = MockTask<DocumentReference>()
    private val saved = slot<Lottery>()
    private var succeeded = false
    private var failed = false

    private lateinit var viewModel: OwnerLotteryCreationViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Lotteries").add(capture(saved)) } returns write.task
        viewModel = OwnerLotteryCreationViewModel()
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    private fun createJulyLottery() {
        viewModel.createAndAddLottery(
            dates = listOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11)),
            title = "July Long Weekend - Banff",
            start = day(2027, 5, 1),
            end = day(2027, 5, 31),
            campsiteIDs = listOf("listing-two-jack", "listing-tunnel-mtn"),
            winnersToDraw = 10,
            waitlistCap = 50,
            onSuccess = { succeeded = true },
            onFailure = { failed = true }
        )
    }

    @Test
    fun getOwnerListings_shouldOnlyReturnThisOwnersCampsites() {
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, tunnelMountain, lakeLouise))

        val listings = viewModel.getOwnerListings(ownerId)

        assertEquals(listOf(twoJack, tunnelMountain), listings)
    }

    @Test
    fun createAndAddLottery_shouldSaveEverythingTheOwnerEntered() {
        createJulyLottery()

        assertEquals("July Long Weekend - Banff", saved.captured.title)
        assertEquals(listOf(day(2027, 7, 9), day(2027, 7, 10), day(2027, 7, 11)), saved.captured.dates)
        assertEquals(day(2027, 5, 1), saved.captured.start)
        assertEquals(day(2027, 5, 31), saved.captured.end)
        assertEquals(listOf("listing-two-jack", "listing-tunnel-mtn"), saved.captured.campsiteIDs)
        assertEquals(10, saved.captured.lotteryWinners)
        assertEquals(50, saved.captured.waitlistCapacity)
    }

    @Test
    fun createAndAddLottery_saveSucceeds_shouldCallOnSuccess() {
        createJulyLottery()

        write.succeed(mockk())

        assertTrue(succeeded)
        assertFalse(failed)
    }

    @Test
    fun createAndAddLottery_saveFails_shouldCallOnFailure() {
        createJulyLottery()

        write.fail()

        assertTrue(failed)
        assertFalse(succeeded)
    }
}
