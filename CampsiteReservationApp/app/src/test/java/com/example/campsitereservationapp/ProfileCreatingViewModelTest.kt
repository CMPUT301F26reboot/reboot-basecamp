/**
 * Test file for ProfileCreatingViewModel
 *
 * Gets device ID, create profile, add to db, retrieves profile and make sure all information
 * is the same
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import android.content.Context
import android.provider.Settings
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.ProfileCreatingViewModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileCreatingViewModelTest {

    /** Exposes the protected functions and the local Profiles copy so the test can use them. */
    private class TestProfileCreatingViewModel : ProfileCreatingViewModel() {
        val profiles: List<Profile> get() = _profiles
        fun save(profile: Profile, onSuccess: () -> Unit, onFailure: () -> Unit) =
            saveProfile(profile, onSuccess, onFailure)
        fun exists(deviceId: String, onResult: (Boolean) -> Unit) =
            profileExists(deviceId, onResult)
    }

    private val deviceId = "device-7f3a91c2"
    private val name = "Alex Chen"
    private val email = "alex@example.com"
    private val phoneNumber = "780-555-0123"
    private val profilePicture = "aGVsbG8="
    private val role = "Entrant"

    private val firestore = MockFirestore()
    private val document = mockk<DocumentReference>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private lateinit var viewModel: TestProfileCreatingViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { firestore.collection("Profiles").document(deviceId) } returns document
        // the device ID comes from Settings.Secure, which is a stub in unit tests
        mockkStatic(Settings.Secure::class)
        every { Settings.Secure.getString(any(), Settings.Secure.ANDROID_ID) } returns deviceId
        viewModel = TestProfileCreatingViewModel()
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    @Test
    fun readDeviceId_returnsAndroidId() {
        assertEquals(deviceId, viewModel.readDeviceId(context))
    }

    @Test
    fun saveProfile_thenRetrieve_allInformationIsTheSame() {
        // create the profile with the device ID
        val profile = Profile(name = name, deviceId = viewModel.readDeviceId(context)).apply {
            email = this@ProfileCreatingViewModelTest.email
            phoneNumber = this@ProfileCreatingViewModelTest.phoneNumber
            profilePicture = this@ProfileCreatingViewModelTest.profilePicture
            notificationsEnabled = false
            role = this@ProfileCreatingViewModelTest.role
        }
        val write = MockTask<Void>()
        val written = slot<Profile>()
        every { document.set(capture(written)) } returns write.task
        var saved = false

        // add to db: written to the document named after the device ID
        viewModel.save(profile, onSuccess = { saved = true }, onFailure = {})
        write.succeed(null)
        assertTrue(saved)

        // retrieve: Firestore sets id to the document name when it reads the profile back
        written.captured.id = deviceId
        firestore.deliver("Profiles", Profile::class.java, listOf(written.captured))
        val retrieved = viewModel.profiles.single()

        assertEquals(deviceId, retrieved.id)
        assertEquals(deviceId, retrieved.deviceId)
        assertEquals(name, retrieved.name)
        assertEquals(email, retrieved.email)
        assertEquals(phoneNumber, retrieved.phoneNumber)
        assertEquals(profilePicture, retrieved.profilePicture)
        assertFalse(retrieved.notificationsEnabled)
        assertEquals(role, retrieved.role)
    }

    @Test
    fun profileExists_savedDevice_returnsTrue() {
        val read = MockTask<DocumentSnapshot>()
        every { document.get() } returns read.task
        var found: Boolean? = null

        viewModel.exists(deviceId) { found = it }
        read.succeed(mockk { every { exists() } returns true })

        assertEquals(true, found)
    }
}