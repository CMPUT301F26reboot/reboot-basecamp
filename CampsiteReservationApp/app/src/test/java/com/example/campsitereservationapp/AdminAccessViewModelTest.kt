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

    private lateinit var profilesRef: CollectionReference
    private lateinit var viewModel: AdminAccessViewModel

    @Before
    fun setUp() {
        val db = mockk<FirebaseFirestore>(relaxed = true)
        profilesRef = mockk(relaxed = true)
        // 1. Mock "Profiles" collection instead of "Admins"
        every { db.collection("Profiles") } returns profilesRef
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        every { Firebase.firestore } returns db

        viewModel = AdminAccessViewModel()
    }

    /** Runs the admin check for [deviceId] and returns [role] in the mocked profile snapshot. */
    private fun checkDevice(deviceId: String, role: String?) {
        val document = mockk<DocumentReference>()
        val lookup = MockTask<DocumentSnapshot>()
        every { profilesRef.document(deviceId) } returns document
        every { document.get() } returns lookup.task

        viewModel.check(deviceId)
        // 2. Mock getString("role") on the DocumentSnapshot
        lookup.succeed(mockk { every { getString("role") } returns role })
    }

    // Check if profile with role = "Admin" sets isAdmin to true
    @Test
    fun check_adminRoleInDatabase_isAdminTrue() {
        checkDevice(adminId, role = "Admin")

        assertTrue(viewModel.isAdmin)
        assertFalse(viewModel.isChecking)
    }

    // Check if profile with non-Admin role sets isAdmin to false
    @Test
    fun check_deviceNotInDatabase_isAdminFalse() {
        checkDevice(userId, role = "Entrant")

        assertFalse(viewModel.isAdmin)
        assertFalse(viewModel.isChecking)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }
}