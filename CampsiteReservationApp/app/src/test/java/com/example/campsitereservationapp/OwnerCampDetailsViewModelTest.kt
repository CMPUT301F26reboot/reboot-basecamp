/*
 * AI use:
 * - Q: How do I test ViewModels that connect to Firebase when they're created?
 *   A: Plain JUnit for the models; MockK to mock Firebase.firestore for the ViewModels.
 * - Some of the mock tests were written using Claude's output: the shared MockFirestore.kt helper
 *   and the ViewModel tests that use it.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import android.content.Context
import android.net.Uri
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.owner.CampsiteRegister.OwnerCampDetailsViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Mock tests for [OwnerCampDetailsViewModel], the controller behind the owner's Register Campsite
 * screen (US 02.01.01). Firestore is mocked with MockK so the ViewModel can be created on the JVM.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class OwnerCampDetailsViewModelTest {

    private val ownerId = "owner-device-7f3a91c2"
    private val context = mockk<Context>(relaxed = true)

    private lateinit var listingsRef: CollectionReference
    private lateinit var viewModel: OwnerCampDetailsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val db = mockk<FirebaseFirestore>(relaxed = true)
        listingsRef = mockk(relaxed = true)
        every { db.collection("Listings") } returns listingsRef
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        every { Firebase.firestore } returns db

        viewModel = OwnerCampDetailsViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
    }

    private fun fillInRequiredFields() {
        viewModel.siteName = "  Two Jack Lakeside "
        viewModel.location = "Lake Minnewanka Scenic Dr, Banff, AB T1L 1K2"
    }

    /**
     * Makes `Listings.add(...)` return a task whose listeners the test can fire. The getters wait
     * for the listeners to be attached, since publish() writes from a background thread.
     */
    private fun stubListingWrite(): Pair<() -> OnSuccessListener<DocumentReference>, () -> OnFailureListener> {
        val task = mockk<Task<DocumentReference>>()
        var onSuccess: OnSuccessListener<DocumentReference>? = null
        var onFailure: OnFailureListener? = null
        every { listingsRef.add(any()) } returns task
        every { task.addOnSuccessListener(any<OnSuccessListener<DocumentReference>>()) } answers { onSuccess = firstArg(); task }
        every { task.addOnFailureListener(any<OnFailureListener>()) } answers { onFailure = firstArg(); task }
        val attached = { verify(timeout = 2_000) { task.addOnFailureListener(any<OnFailureListener>()) } }
        return { attached(); onSuccess!! } to { attached(); onFailure!! }
    }

    @Test
    fun canPublish_shouldRequireSiteNameAndAddress() {
        assertFalse(viewModel.canPublish)

        viewModel.siteName = "Two Jack Lakeside"
        assertFalse(viewModel.canPublish)

        viewModel.location = "Lake Minnewanka Scenic Dr, Banff, AB T1L 1K2"
        assertTrue(viewModel.canPublish)
    }

    @Test
    fun decrementOccupancy_atOneGuest_shouldStayAtOne() {
        viewModel.decrementOccupancy()

        assertEquals(1, viewModel.maxOccupancy)
    }

    @Test
    fun addTag_shouldTrimAndIgnoreDuplicates() {
        viewModel.addTag("  Lakefront ")
        viewModel.addTag("Lakefront")
        viewModel.addTag("   ")

        assertEquals(listOf("Lakefront"), viewModel.tags)
    }

    @Test
    fun addPhotos_moreThanFour_shouldKeepFirstFour() {
        val picked = List(6) { mockk<Uri>() }

        viewModel.addPhotos(picked)

        assertEquals(picked.take(4), viewModel.photos)
    }

    @Test
    fun publish_missingAddress_shouldNotWriteToFirestore() {
        viewModel.siteName = "Two Jack Lakeside"

        viewModel.publish(ownerId, context)

        assertFalse(viewModel.isPublishing)
        verify(exactly = 0) { listingsRef.add(any()) }
    }

    @Test
    fun publish_validForm_shouldWriteTrimmedListingForOwner() {
        val written = slot<Listing>()
        fillInRequiredFields()
        viewModel.incrementOccupancy()
        viewModel.addTag("Lakefront")

        viewModel.publish(ownerId, context)

        verify(timeout = 2_000) { listingsRef.add(capture(written)) }
        assertEquals("Two Jack Lakeside", written.captured.name)
        assertEquals("Lake Minnewanka Scenic Dr, Banff, AB T1L 1K2", written.captured.address)
        assertEquals(2, written.captured.maxOccupancy)
        assertEquals(ownerId, written.captured.ownerID)
        assertEquals(listOf("Lakefront"), written.captured.tags)
    }

    @Test
    fun publish_writeSucceeds_shouldMarkPublished() {
        val (onSuccess, _) = stubListingWrite()
        fillInRequiredFields()

        viewModel.publish(ownerId, context)
        onSuccess().onSuccess(mockk())

        assertFalse(viewModel.isPublishing)
        assertTrue(viewModel.isPublished)
    }

    @Test
    fun publish_writeFails_shouldShowErrorAndAllowRetry() {
        val (_, onFailure) = stubListingWrite()
        fillInRequiredFields()

        viewModel.publish(ownerId, context)
        onFailure().onFailure(Exception("Network unavailable"))

        assertEquals("Couldn't publish the listing. Please try again.", viewModel.errorMessage)
        assertFalse(viewModel.isPublished)
        assertTrue(viewModel.canPublish)
    }
}
