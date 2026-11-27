/**
 * This file tests CampsiteImagesViewModel
 *
 * Tests:
 * - Looking up a listing by its ID returns that listing.
 * - Looking up an ID that doesn't exist returns null.
 * - A listing's images include both Base64 images and URL links.
 * - A listing that doesn't exist has no images.
 * - Deleting a URL image removes that link from the listing in Firestore and calls onSuccess.
 * - Deleting a Base64 image removes that image from the listing in Firestore and calls onSuccess.
 * - A failed delete calls onFailure.
 * - A blank listing ID or blank image is never sent to Firestore, and neither callback runs.
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-26_AdminBrowseAndDelete.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.ui.Admin.CampsiteImages.CampsiteImagesViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Mock tests for [CampsiteImagesViewModel], which lets an admin view and delete a campsite's images. */
class CampsiteImagesViewModelTest {

    private val base64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg=="
    private val urlImage = "https://example.com/photos/two-jack.jpg"

    private val twoJack = Listing(
        name = "Two Jack Lakeside",
        photoUrls = listOf(base64Image, urlImage)
    ).apply { id = "listing-two-jack" }

    private val firestore = MockFirestore()
    private val listingDoc = mockk<DocumentReference>()
    private val removal = mockk<FieldValue>()
    private val write = MockTask<Void>()
    private lateinit var viewModel: CampsiteImagesViewModel

    @Before
    fun setUp() {
        firestore.install()
        mockkStatic(FieldValue::class) // undone by firestore.uninstall()
        every { FieldValue.arrayRemove(*anyVararg()) } returns removal
        every { firestore.collection("Listings").document(any()) } returns listingDoc
        every { listingDoc.update("photoUrls", any()) } returns write.task

        viewModel = CampsiteImagesViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun getListing_knownId_shouldReturnThatListing() {
        assertEquals(twoJack, viewModel.getListing("listing-two-jack"))
    }

    @Test
    fun getListing_unknownId_shouldReturnNull() {
        assertNull(viewModel.getListing("listing-missing"))
    }

    @Test
    fun getImages_shouldReturnBothBase64AndUrlImages() {
        assertEquals(listOf(base64Image, urlImage), viewModel.getImages("listing-two-jack"))
    }

    @Test
    fun getImages_unknownListing_shouldReturnEmptyList() {
        assertEquals(emptyList<String>(), viewModel.getImages("listing-missing"))
    }

    @Test
    fun deleteImage_url_shouldRemoveLinkAndCallOnSuccess() {
        var succeeded = false
        var failed = false

        viewModel.deleteImage("listing-two-jack", urlImage, onSuccess = { succeeded = true }, onFailure = { failed = true })
        write.succeed(null)

        verify { firestore.collection("Listings").document("listing-two-jack") }
        verify { FieldValue.arrayRemove(urlImage) }
        verify { listingDoc.update("photoUrls", removal) }
        assertTrue(succeeded)
        assertFalse(failed)
    }

    @Test
    fun deleteImage_base64_shouldRemoveImageAndCallOnSuccess() {
        var succeeded = false

        viewModel.deleteImage("listing-two-jack", base64Image, onSuccess = { succeeded = true })
        write.succeed(null)

        verify { FieldValue.arrayRemove(base64Image) }
        verify { listingDoc.update("photoUrls", removal) }
        assertTrue(succeeded)
    }

    @Test
    fun deleteImage_failure_shouldCallOnFailure() {
        var succeeded = false
        var failed = false

        viewModel.deleteImage("listing-two-jack", urlImage, onSuccess = { succeeded = true }, onFailure = { failed = true })
        write.fail()

        assertFalse(succeeded)
        assertTrue(failed)
    }

    @Test
    fun deleteImage_blankInput_shouldNotTouchFirestore() {
        var called = false

        viewModel.deleteImage("", urlImage, onSuccess = { called = true }, onFailure = { called = true })
        viewModel.deleteImage("listing-two-jack", "", onSuccess = { called = true }, onFailure = { called = true })

        verify(exactly = 0) { listingDoc.update(any<String>(), any()) }
        assertFalse(called)
    }
}