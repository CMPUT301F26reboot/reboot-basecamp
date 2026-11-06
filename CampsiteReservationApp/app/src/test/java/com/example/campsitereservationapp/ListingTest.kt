/*
 * AI use (full log: "AI Use Logs - Jeel/AI-Use-Log_Jeel.md"):
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.google.firebase.firestore.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests for [Listing], the campsite model created by the owner's Register Campsite screen and
 * shown on the owner's My Campsites list.
 */
class ListingTest {

    private val name = "Two Jack Lakeside"
    private val address = "Lake Minnewanka Scenic Dr, Banff, AB T1L 1K2"
    private val maxOccupancy = 6
    private val description = "Walk-in tent pads on the shore of Two Jack Lake. Check-in after 2 PM."
    private val ownerId = "owner-device-7f3a91c2"
    private val tags = listOf("Lakefront", "Tent only", "Fire pit")
    private val geoLocation = GeoPoint(51.2290, -115.5040)
    private val rules = "Quiet hours 10 PM - 7 AM. Store all food in bear lockers."

    private lateinit var listing: Listing

    @Before
    fun setUp() {
        listing = Listing(
            name = name,
            address = address,
            maxOccupancy = maxOccupancy,
            description = description,
            ownerID = ownerId,
            tags = tags,
            geoLocation = geoLocation,
            rules = rules,
            rulesAcceptanceRequired = true
        )
    }

    @Test
    fun constructor_shouldStoreRegisteredDetails() {
        assertEquals(name, listing.name)
        assertEquals(address, listing.address)
        assertEquals(maxOccupancy, listing.maxOccupancy)
        assertEquals(description, listing.description)
        assertEquals(ownerId, listing.ownerID)
        assertEquals(tags, listing.tags)
        assertEquals(geoLocation, listing.geoLocation)
        assertEquals(rules, listing.rules)
        assertTrue(listing.rulesAcceptanceRequired)
    }

    @Test
    fun newListing_shouldStartUnrated() {
        assertEquals(0.0, listing.rating, 0.0)
        assertEquals(0, listing.numOfReviews)
    }

    @Test
    fun noArgConstructor_shouldUseFirestoreSafeDefaults() {
        val empty = Listing()

        assertEquals("", empty.name)
        assertEquals("", empty.id)
        assertTrue(empty.tags.isEmpty())
        assertTrue(empty.photoUrls.isEmpty())
        assertEquals(GeoPoint(0.0, 0.0), empty.geoLocation)
        assertTrue(empty.rulesAcceptanceRequired)
    }

    @Test
    fun setMaxOccupancy_shouldChangeMaxOccupancy() {
        listing.maxOccupancy = 8

        assertEquals(8, listing.maxOccupancy)
    }

    @Test
    fun setRulesAcceptanceRequired_shouldChangeRequirement() {
        listing.rulesAcceptanceRequired = false

        assertFalse(listing.rulesAcceptanceRequired)
    }

    @Test
    fun setTags_shouldReplaceTags() {
        val newTags = listOf("RV", "Power hookup")

        listing.tags = newTags

        assertEquals(newTags, listing.tags)
    }

    @Test
    fun setRating_shouldChangeRatingAndReviewCount() {
        listing.rating = 4.7
        listing.numOfReviews = 23

        assertEquals(4.7, listing.rating, 0.0)
        assertEquals(23, listing.numOfReviews)
    }

    @Test
    fun setId_shouldChangeId() {
        listing.id = "listing-two-jack"

        assertEquals("listing-two-jack", listing.id)
    }
}
