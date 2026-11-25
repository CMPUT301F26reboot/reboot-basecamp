/**
 * Test for AdminAccessViewModel,
 * Test:
 *  -isAdmin should be false if not in database
 *  -isAdmin should be true if in database
 *  -isChecking should always be false after every test above
 *
 * AI Usage Reference : AI Use Logs - Jacob\Claude-Chat-Log_2026-11-24_AdminHome_AdminRoleSwitch.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.ui.Admin.AdminSetup.AdminAccessViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdminAccessViewModelTest {

    private val adminId = "temp-admin-device"
    private val userId = "temp-user-device"

    /** Device IDs that have a document in the mocked `Admins` collection. */
    private val admins = mutableSetOf<String>()

    private lateinit var adminsRef: CollectionReference
    private lateinit var viewModel: AdminAccessViewModel

    //test setup
    @Before
    fun setUp() {
        val db = mockk<FirebaseFirestore>(relaxed = true)
        adminsRef = mockk(relaxed = true)
        every { db.collection("Admins") } returns adminsRef
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        every { Firebase.firestore } returns db

        //Create an temporary admin profile
        admins.add(adminId)

        viewModel = AdminAccessViewModel()
    }

    /** Runs the admin check for [deviceId] and finishes the Firestore lookup. */
    private fun checkDevice(deviceId: String) {
        val document = mockk<DocumentReference>()
        val lookup = MockTask<DocumentSnapshot>()
        every { adminsRef.document(deviceId) } returns document
        every { document.get() } returns lookup.task

        viewModel.check(deviceId)
        lookup.succeed(mockk { every { exists() } returns (deviceId in admins) })
    }

    //check if admin is in database
    @Test
    fun check_adminInDatabase_isAdminTrue() {
        checkDevice(adminId)

        assertTrue(viewModel.isAdmin)
        assertFalse(viewModel.isChecking)
    }

    //check if admin is not in database
    @Test
    fun check_deviceNotInDatabase_isAdminFalse() {
        checkDevice(userId)

        assertFalse(viewModel.isAdmin)
        assertFalse(viewModel.isChecking)
    }

    //delete temporary admin profile
    @After
    fun tearDown() {
        admins.remove(adminId)
        unmockkAll()
    }
}