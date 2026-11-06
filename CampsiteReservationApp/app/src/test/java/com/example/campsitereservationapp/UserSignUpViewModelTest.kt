package com.example.campsitereservationapp

import android.content.Context
import android.provider.Settings
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.ui.entrant.SignUp.UserSignUpViewModel
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests for [UserSignUpViewModel] and the ProfileCreatingViewModel it inherits from (US 01.09.01).
 * Firestore is mocked with MockK so that the ViewModel can be created without Firebase running.
 */
class UserSignUpViewModelTest {

    private val deviceId = "device-123"

    private lateinit var document: DocumentReference
    private lateinit var viewModel: UserSignUpViewModel

    @Before
    fun setUp() {
        val db = mockk<FirebaseFirestore>(relaxed = true)
        val profilesRef = mockk<CollectionReference>(relaxed = true)
        document = mockk(relaxed = true)
        every { db.collection("Profiles") } returns profilesRef
        every { profilesRef.document(deviceId) } returns document
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        every { Firebase.firestore } returns db

        viewModel = UserSignUpViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    /** A mocked Firestore call whose result the test decides by calling its listeners. */
    private class FakeTask<T> {
        val task = mockk<Task<T>>()
        var onSuccess: OnSuccessListener<T>? = null
        var onFailure: OnFailureListener? = null

        init {
            every { task.addOnSuccessListener(any<OnSuccessListener<T>>()) } answers {
                onSuccess = firstArg()
                task
            }
            every { task.addOnFailureListener(any<OnFailureListener>()) } answers {
                onFailure = firstArg()
                task
            }
        }
    }

    /** Makes writing the profile return a task that the test can complete. */
    private fun stubWrite(): FakeTask<Void> {
        val write = FakeTask<Void>()
        every { document.set(any()) } returns write.task
        return write
    }

    /** Makes reading the profile return a task that the test can complete. */
    private fun stubRead(): FakeTask<DocumentSnapshot> {
        val read = FakeTask<DocumentSnapshot>()
        every { document.get() } returns read.task
        return read
    }

    /** Fills in every field with valid text that has spaces around it. */
    private fun fillInValidForm() {
        viewModel.name = "  Alex Tremblay "
        viewModel.email = " alex@example.com "
        viewModel.phoneNumber = " +1 (403) 555-0199 "
    }

    @Test
    fun readDeviceId_returnsAndroidId() {
        mockkStatic(Settings.Secure::class)
        every { Settings.Secure.getString(any(), Settings.Secure.ANDROID_ID) } returns deviceId

        assertEquals(deviceId, viewModel.readDeviceId(mockk<Context>(relaxed = true)))
    }

    @Test
    fun checkDevice_existingProfile_signsInAutomatically() {
        val read = stubRead()
        val snapshot = mockk<DocumentSnapshot> { every { exists() } returns true }

        viewModel.checkDevice(deviceId)
        assertTrue(viewModel.isCheckingDevice)
        read.onSuccess!!.onSuccess(snapshot)

        assertFalse(viewModel.isCheckingDevice)
        assertTrue(viewModel.isSignedUp)
    }

    @Test
    fun checkDevice_noProfile_showsForm() {
        val read = stubRead()
        val snapshot = mockk<DocumentSnapshot> { every { exists() } returns false }

        viewModel.checkDevice(deviceId)
        read.onSuccess!!.onSuccess(snapshot)

        assertFalse(viewModel.isCheckingDevice)
        assertFalse(viewModel.isSignedUp)
    }

    @Test
    fun checkDevice_readFails_showsForm() {
        val read = stubRead()

        viewModel.checkDevice(deviceId)
        read.onFailure!!.onFailure(Exception("Network unavailable"))

        assertFalse(viewModel.isCheckingDevice)
        assertFalse(viewModel.isSignedUp)
    }

    @Test
    fun signUp_emptyForm_showsEveryErrorAndDoesNotWrite() {
        viewModel.signUp(deviceId)

        assertNotNull(viewModel.nameError)
        assertNotNull(viewModel.emailError)
        assertNotNull(viewModel.phoneNumberError)
        assertFalse(viewModel.isSaving)
        verify(exactly = 0) { document.set(any()) }
    }

    @Test
    fun signUp_invalidEmails_showEmailErrorAndDoNotWrite() {
        fillInValidForm()

        for (email in listOf("abc", "abc@", "@example.com", "abc@example", "a b@example.com")) {
            viewModel.email = email
            viewModel.signUp(deviceId)

            assertNotNull(email, viewModel.emailError)
        }
        verify(exactly = 0) { document.set(any()) }
    }

    @Test
    fun signUp_invalidPhoneNumbers_showPhoneErrorAndDoNotWrite() {
        fillInValidForm()

        for (phone in listOf("", "abc", "12345", "780123456a", "1234567890123456")) {
            viewModel.phoneNumber = phone
            viewModel.signUp(deviceId)

            assertNotNull(phone, viewModel.phoneNumberError)
        }
        verify(exactly = 0) { document.set(any()) }
    }

    @Test
    fun signUp_validPhoneFormats_areAccepted() {
        stubWrite()
        fillInValidForm()

        for (phone in listOf("7801234567", "780-123-4567", "780.123.4567", "+1 (403) 555-0199")) {
            viewModel.phoneNumber = phone
            viewModel.signUp(deviceId)

            assertNull(phone, viewModel.phoneNumberError)
        }
    }

    @Test
    fun signUp_correctedForm_clearsEarlierErrors() {
        stubWrite()
        viewModel.signUp(deviceId)

        fillInValidForm()
        viewModel.signUp(deviceId)

        assertNull(viewModel.nameError)
        assertNull(viewModel.emailError)
        assertNull(viewModel.phoneNumberError)
    }

    @Test
    fun signUp_validForm_writesTrimmedProfileToDeviceDocument() {
        val written = slot<Any>()
        every { document.set(capture(written)) } returns FakeTask<Void>().task
        fillInValidForm()

        viewModel.signUp(deviceId)

        val profile = written.captured as Profile
        assertEquals("Alex Tremblay", profile.name)
        assertEquals("alex@example.com", profile.email)
        assertEquals("+1 (403) 555-0199", profile.phoneNumber)
        assertEquals(deviceId, profile.deviceId)
    }

    @Test
    fun signUp_whileSaving_doesNotWriteTwice() {
        stubWrite()
        fillInValidForm()

        viewModel.signUp(deviceId)
        viewModel.signUp(deviceId)

        assertTrue(viewModel.isSaving)
        verify(exactly = 1) { document.set(any()) }
    }

    @Test
    fun signUp_writeSucceeds_marksSignedUp() {
        val write = stubWrite()
        fillInValidForm()

        viewModel.signUp(deviceId)
        write.onSuccess!!.onSuccess(null)

        assertFalse(viewModel.isSaving)
        assertTrue(viewModel.isSignedUp)
        assertNull(viewModel.errorMessage)
    }

    @Test
    fun signUp_writeFails_showsErrorAndAllowsRetry() {
        val write = stubWrite()
        fillInValidForm()

        viewModel.signUp(deviceId)
        write.onFailure!!.onFailure(Exception("Network unavailable"))

        assertFalse(viewModel.isSaving)
        assertFalse(viewModel.isSignedUp)
        assertNotNull(viewModel.errorMessage)
    }

    @Test
    fun clearError_removesErrorMessage() {
        val write = stubWrite()
        fillInValidForm()
        viewModel.signUp(deviceId)
        write.onFailure!!.onFailure(Exception("Network unavailable"))

        viewModel.clearError()

        assertNull(viewModel.errorMessage)
    }
}
