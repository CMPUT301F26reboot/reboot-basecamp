/**
 * This file tests AdminBrowseProfileViewModel
 *
 * Tests:
 * - An empty search shows every profile.
 * - A keyword search shows only profiles whose name starts with the keyword, ignoring case.
 * - A successful delete removes the right profile from Firestore and the search results,
 *   and calls onSuccess.
 * - A failed delete keeps the profile in the search results and calls onFailure.
 * - A profile with no ID is never sent to Firestore, and neither callback runs.
 * - make sure profile picture is deleted when they click delete profile picture button
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-26_AdminBrowseAndDelete.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Reservation
import com.example.campsitereservationapp.ui.Admin.BrowseProfile.AdminBrowseProfileViewModel
import com.google.firebase.firestore.DocumentReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Mock tests for [AdminBrowseProfileViewModel], which lets an admin search and delete profiles. */
class AdminBrowseProfileViewModelTest {

    private val jane = Profile(name = "Jane Doe", deviceId = "device-jane").apply { id = "device-jane" }
    private val john = Profile(name = "John Smith", deviceId = "device-john").apply { id = "device-john" }

    private val firestore = MockFirestore()
    private val profileDoc = mockk<DocumentReference>()
    private val deletion = MockTask<Void>()
    private lateinit var viewModel: AdminBrowseProfileViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Profiles").document(any()) } returns profileDoc
        every { profileDoc.delete() } returns deletion.task

        viewModel = AdminBrowseProfileViewModel()
        firestore.deliver("Profiles", Profile::class.java, listOf(jane, john))
        viewModel.profileSearch("")
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun profileSearch_emptyKeyword_shouldShowEveryProfile() {
        assertEquals(listOf(jane, john), viewModel._searchResults)
    }

    @Test
    fun profileSearch_keyword_shouldMatchStartOfNameIgnoringCase() {
        viewModel.profileSearch("JOHN")

        assertEquals(listOf(john), viewModel._searchResults)
    }

    @Test
    fun profileDelete_success_shouldRemoveProfileAndCallOnSuccess() {
        var succeeded = false
        var failed = false

        viewModel.profileDelete(jane, onSuccess = { succeeded = true }, onFailure = { failed = true })
        deletion.succeed(null)

        verify { firestore.collection("Profiles").document("device-jane") }
        assertEquals(listOf(john), viewModel._searchResults)
        assertTrue(succeeded)
        assertFalse(failed)
    }

    @Test
    fun profileDelete_failure_shouldKeepProfileAndCallOnFailure() {
        var succeeded = false
        var failed = false

        viewModel.profileDelete(jane, onSuccess = { succeeded = true }, onFailure = { failed = true })
        deletion.fail()

        assertEquals(listOf(jane, john), viewModel._searchResults)
        assertFalse(succeeded)
        assertTrue(failed)
    }

    @Test
    fun profileDelete_blankId_shouldNotTouchFirestore() {
        var called = false
        val unsaved = Profile(name = "Unsaved User") // id is "" until Firestore assigns one

        viewModel.profileDelete(unsaved, onSuccess = { called = true }, onFailure = { called = true })

        verify(exactly = 0) { profileDoc.delete() }
        assertEquals(listOf(jane, john), viewModel._searchResults)
        assertFalse(called)
    }
    @Test
    fun profilePictureDelete_success_shouldClearPictureAndCallOnSuccess() {
        var succeeded = false
        var failed = false
        val clearPicture = MockTask<Void>()
        every { profileDoc.update("profilePicture", "") } returns clearPicture.task
        jane.profilePicture = "https://example.com/photos/jane.jpg"

        viewModel.profilePictureDelete(jane, onSuccess = { succeeded = true }, onFailure = { failed = true })
        clearPicture.succeed(null)

        verify { firestore.collection("Profiles").document("device-jane") }
        verify { profileDoc.update("profilePicture", "") }
        assertTrue(succeeded)
        assertFalse(failed)
        assertEquals("", jane.profilePicture) // card stops showing the old photo
    }

    @Test
    fun profileDelete_success_shouldTakeProfileOffWaitlists() {
        val lotteryDoc = mockk<DocumentReference>(relaxed = true)
        every { firestore.collection("Lotteries").document("lottery-banff") } returns lotteryDoc
        val joined = Lottery().apply { id = "lottery-banff"; waitlist = listOf("device-jane", "device-john") }
        val notJoined = Lottery().apply { id = "lottery-other"; waitlist = listOf("device-john") }
        firestore.deliver("Lotteries", Lottery::class.java, listOf(joined, notJoined))

        viewModel.profileDelete(jane, onSuccess = {}, onFailure = {})
        deletion.succeed(null)

        verify { lotteryDoc.update("waitlist", any()) }
        verify(exactly = 0) { firestore.collection("Lotteries").document("lottery-other") }
    }

    @Test
    fun profileDelete_success_shouldCancelReservationsThatHaveNotFinished() {
        val futureDoc = mockk<DocumentReference>(relaxed = true)
        val ongoingDoc = mockk<DocumentReference>(relaxed = true)
        val pastDoc = mockk<DocumentReference>(relaxed = true)
        val othersDoc = mockk<DocumentReference>(relaxed = true)
        val reservations = firestore.collection("Reservations")
        every { reservations.document("res-future") } returns futureDoc
        every { reservations.document("res-ongoing") } returns ongoingDoc
        every { reservations.document("res-past") } returns pastDoc
        every { reservations.document("res-john") } returns othersDoc
        firestore.deliver("Reservations", Reservation::class.java, listOf(
            Reservation(listOf(daysFromNow(10)), "device-jane", "listing-a").apply { id = "res-future" },
            Reservation(listOf(daysFromNow(-1), daysFromNow(1)), "device-jane", "listing-a").apply { id = "res-ongoing" },
            Reservation(listOf(daysFromNow(-30)), "device-jane", "listing-a").apply { id = "res-past" },
            Reservation(listOf(daysFromNow(10)), "device-john", "listing-a").apply { id = "res-john" },
        ))

        viewModel.profileDelete(jane, onSuccess = {}, onFailure = {})
        deletion.succeed(null)

        verify { futureDoc.update("status", "cancelled") }
        verify { ongoingDoc.update("status", "cancelled") }
        verify(exactly = 0) { pastDoc.update(any<String>(), any()) }   // history is kept
        verify(exactly = 0) { othersDoc.update(any<String>(), any()) } // other users untouched
    }
}
