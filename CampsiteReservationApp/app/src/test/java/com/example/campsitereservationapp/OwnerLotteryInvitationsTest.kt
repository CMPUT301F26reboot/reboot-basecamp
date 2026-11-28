package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Listing
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Lottery
import com.example.campsitereservationapp.ui.owner.Lotterylist.OwnerLotteryListViewModel
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

/**
 * Mock tests for the multi-owner part of [OwnerLotteryListViewModel] (US 02.04.08): lotteries
 * shared between owners, the invitations shown on the invitations screen, and accepting,
 * declining or withdrawing campsites.
 */
class OwnerLotteryInvitationsTest {

    private val host = "owner-device-7f3a91c2"
    private val guest = "owner-device-22be0d14"

    private val twoJack = Listing(name = "Two Jack Lakeside", ownerID = host, ownerName = "Priya").apply { id = "listing-two-jack" }
    private val lakeLouise = Listing(name = "Lake Louise Hard-Sided", ownerID = guest, ownerName = "Sam").apply { id = "listing-lake-louise" }
    private val moraine = Listing(name = "Moraine Lake Tent Pad", ownerID = guest, ownerName = "Sam").apply { id = "listing-moraine" }

    private val firestore = MockFirestore()
    private val lotteryDoc = mockk<DocumentReference>(relaxed = true)
    private lateinit var viewModel: OwnerLotteryListViewModel

    /** An open lottery created by [host] on Two Jack, inviting the guest's Lake Louise campsite. */
    private fun sharedLottery(
        accepted: Boolean = false,
        end: java.util.Date = daysFromNow(10)
    ) = Lottery(listOf(daysFromNow(20)), daysFromNow(-1), end,
        if (accepted) listOf("listing-two-jack", "listing-lake-louise") else listOf("listing-two-jack")
    ).apply {
        id = "lottery-shared"
        title = "Shared Banff Weekend"
        ownerID = host
        pendingCampsiteIDs = if (accepted) emptyList() else listOf("listing-lake-louise")
    }

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Lotteries").document("lottery-shared") } returns lotteryDoc
        viewModel = OwnerLotteryListViewModel()
        firestore.deliver("Listings", Listing::class.java, listOf(twoJack, lakeLouise, moraine))
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun invitations_pendingCampsite_shouldShowForItsOwnerOnly() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(sharedLottery()))

        val invites = viewModel.invitations(guest)

        assertEquals(1, invites.size)
        assertEquals("listing-lake-louise", invites.first().campsiteId)
        assertEquals("Priya", invites.first().hostName)
        assertTrue(viewModel.invitations(host).isEmpty())
    }

    @Test
    fun invitations_cancelledOrClosedLottery_shouldBeHidden() {
        val cancelled = sharedLottery().apply { cancelled = true }
        firestore.deliver("Lotteries", Lottery::class.java, listOf(cancelled))
        assertTrue(viewModel.invitations(guest).isEmpty())

        firestore.deliver("Lotteries", Lottery::class.java, listOf(sharedLottery(end = daysFromNow(-1))))
        assertTrue(viewModel.invitations(guest).isEmpty())
    }

    @Test
    fun ownerLotteries_pendingInvite_shouldNotListLotteryForGuestYet() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(sharedLottery()))

        assertTrue(viewModel.ownerLotteries(guest).isEmpty())
    }

    @Test
    fun ownerLotteries_acceptedInvite_shouldListForGuestAsParticipant() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(sharedLottery(accepted = true)))

        val item = viewModel.ownerLotteries(guest).single()

        assertFalse(item.isCreator)
        assertEquals("Priya", item.hostName)
        assertEquals(listOf("listing-lake-louise"), item.myCampsiteIDs)
        assertEquals("Two Jack Lakeside +1 more", item.campsiteName)
    }

    @Test
    fun ownerLotteries_creator_shouldBeAbleToManage() {
        firestore.deliver("Lotteries", Lottery::class.java, listOf(sharedLottery(accepted = true)))

        assertTrue(viewModel.ownerLotteries(host).single().isCreator)
    }

    @Test
    fun ownerLotteries_olderLotteryWithoutCreator_shouldTreatCampsiteOwnerAsCreator() {
        val old = sharedLottery().apply { ownerID = "" }
        firestore.deliver("Lotteries", Lottery::class.java, listOf(old))

        assertTrue(viewModel.ownerLotteries(host).single().isCreator)
    }

    @Test
    fun acceptInvite_shouldMoveCampsiteFromPendingIntoLottery() {
        viewModel.acceptInvite("lottery-shared", "listing-lake-louise", onSuccess = {}, onFailure = {})

        verify {
            lotteryDoc.update(match<Map<String, Any>> {
                it.keys == setOf("pendingCampsiteIDs", "campsiteIDs")
            })
        }
    }

    @Test
    fun declineInvite_shouldRemoveCampsiteFromPending() {
        viewModel.declineInvite("lottery-shared", "listing-lake-louise", onSuccess = {}, onFailure = {})

        verify { lotteryDoc.update("pendingCampsiteIDs", any()) }
    }

    @Test
    fun withdrawCampsites_shouldRemoveCampsitesFromLottery() {
        viewModel.withdrawCampsites("lottery-shared", listOf("listing-lake-louise"), onSuccess = {}, onFailure = {})

        verify { lotteryDoc.update("campsiteIDs", any()) }
    }
}
