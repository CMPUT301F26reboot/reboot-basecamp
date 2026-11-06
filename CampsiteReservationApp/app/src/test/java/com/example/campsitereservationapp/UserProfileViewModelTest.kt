package com.example.campsitereservationapp

import android.content.Context
import android.net.Uri
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.ui.entrant.UserProfile.ProfileField
import com.example.campsitereservationapp.ui.entrant.UserProfile.UserProfileViewModel
import com.example.campsitereservationapp.utils.encodePhotoToBase64
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.firestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Tests for [UserProfileViewModel] (US 01.07.03, US 01.08.01, US 01.10.01). Firestore is mocked
 * with MockK so that the ViewModel can be created without Firebase running.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileViewModelTest {

    private val profileId = "device-123"

    private lateinit var db: FirebaseFirestore
    private lateinit var lotteriesRef: CollectionReference
    private lateinit var document: DocumentReference
    private lateinit var viewModel: UserProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        db = mockk(relaxed = true)
        val profilesRef = mockk<CollectionReference>(relaxed = true)
        lotteriesRef = mockk(relaxed = true)
        document = mockk(relaxed = true)
        every { db.collection("Profiles") } returns profilesRef
        every { db.collection("Lotteries") } returns lotteriesRef
        every { profilesRef.document(profileId) } returns document
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        every { Firebase.firestore } returns db

        viewModel = UserProfileViewModel()
    }

    @After
    fun tearDown() {
        unmockkAll()
        Dispatchers.resetMain()
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

    /** Makes updating [key] to [value] return a task that the test can complete. */
    private fun stubUpdate(key: String, value: Any): FakeTask<Void> {
        val write = FakeTask<Void>()
        every { document.update(key, value) } returns write.task
        return write
    }

    /** Loads a saved profile into the ViewModel. */
    private fun loadProfile() {
        val profile = Profile(name = "Alex Tremblay", deviceId = profileId)
        profile.email = "alex@example.com"
        profile.phoneNumber = "+1 (403) 555-0199"
        profile.notificationsEnabled = false

        val read = FakeTask<DocumentSnapshot>()
        every { document.get() } returns read.task
        viewModel.load(profileId)
        read.onSuccess!!.onSuccess(
            mockk { every { toObject(Profile::class.java) } returns profile }
        )
    }

    @Test
    fun load_success_showsSavedValues() {
        loadProfile()

        assertFalse(viewModel.isLoading)
        assertEquals("Alex Tremblay", viewModel.name)
        assertEquals("alex@example.com", viewModel.email)
        assertEquals("+1 (403) 555-0199", viewModel.phoneNumber)
        assertFalse(viewModel.notificationsEnabled)
    }

    @Test
    fun load_failure_showsMessage() {
        val read = FakeTask<DocumentSnapshot>()
        every { document.get() } returns read.task

        viewModel.load(profileId)
        read.onFailure!!.onFailure(Exception("Network unavailable"))

        assertFalse(viewModel.isLoading)
        assertNotNull(viewModel.message)
    }

    @Test
    fun startEditing_startsWithSavedValue() {
        loadProfile()

        viewModel.startEditing(ProfileField.EMAIL)

        assertEquals(ProfileField.EMAIL, viewModel.editingField)
        assertEquals("alex@example.com", viewModel.draft)
    }

    @Test
    fun saveEdit_validValue_writesOnlyThatFieldAndConfirms() {
        loadProfile()
        val write = stubUpdate("name", "Jane Dough")

        viewModel.startEditing(ProfileField.NAME)
        viewModel.draft = "  Jane Dough "
        viewModel.saveEdit()
        assertTrue(viewModel.isSaving)
        write.onSuccess!!.onSuccess(null)

        assertEquals("Jane Dough", viewModel.name)
        assertNull(viewModel.editingField)
        assertFalse(viewModel.isSaving)
        assertEquals("Changes saved", viewModel.message)
    }

    @Test
    fun saveEdit_everyField_writesToItsFirestoreKey() {
        loadProfile()
        val edits = mapOf(
            ProfileField.EMAIL to ("email" to "jane@example.com"),
            ProfileField.PHONE_NUMBER to ("phoneNumber" to "780-123-4567")
        )

        for ((field, edit) in edits) {
            val write = stubUpdate(edit.first, edit.second)
            viewModel.startEditing(field)
            viewModel.draft = edit.second
            viewModel.saveEdit()
            write.onSuccess!!.onSuccess(null)

            assertEquals(edit.second, viewModel.valueOf(field))
        }
    }

    @Test
    fun saveEdit_invalidValues_showErrorAndDoNotWrite() {
        loadProfile()
        val invalid = mapOf(
            ProfileField.NAME to "  ",
            ProfileField.EMAIL to "not an email",
            ProfileField.PHONE_NUMBER to "12345"
        )

        for ((field, value) in invalid) {
            viewModel.startEditing(field)
            viewModel.draft = value
            viewModel.saveEdit()

            assertNotNull(field.label, viewModel.fieldError)
            assertEquals(field, viewModel.editingField)
        }
        assertEquals("alex@example.com", viewModel.email)
        verify(exactly = 0) { document.update(any<String>(), any()) }
    }

    @Test
    fun saveEdit_unchangedValue_closesWithoutWriting() {
        loadProfile()

        viewModel.startEditing(ProfileField.NAME)
        viewModel.saveEdit()

        assertNull(viewModel.editingField)
        assertNull(viewModel.message)
        verify(exactly = 0) { document.update(any<String>(), any()) }
    }

    @Test
    fun saveEdit_writeFails_keepsOldValueAndShowsMessage() {
        loadProfile()
        val write = stubUpdate("name", "Jane Dough")

        viewModel.startEditing(ProfileField.NAME)
        viewModel.draft = "Jane Dough"
        viewModel.saveEdit()
        write.onFailure!!.onFailure(Exception("Network unavailable"))

        assertEquals("Alex Tremblay", viewModel.name)
        assertEquals(ProfileField.NAME, viewModel.editingField)
        assertFalse(viewModel.isSaving)
        assertNotNull(viewModel.message)
    }

    @Test
    fun updateNotifications_writesSetting() {
        loadProfile()
        stubUpdate("notificationsEnabled", true)

        viewModel.updateNotifications(true)

        assertTrue(viewModel.notificationsEnabled)
        verify { document.update("notificationsEnabled", true) }
    }

    @Test
    fun updateNotifications_writeFails_goesBackToOldSetting() {
        loadProfile()
        val write = stubUpdate("notificationsEnabled", true)

        viewModel.updateNotifications(true)
        write.onFailure!!.onFailure(Exception("Network unavailable"))

        assertFalse(viewModel.notificationsEnabled)
        assertNotNull(viewModel.message)
    }

    @Test
    fun updateProfilePicture_savesEncodedPhoto() {
        loadProfile()
        val context = mockk<Context>(relaxed = true)
        val uri = mockk<Uri>()
        mockkStatic("com.example.campsitereservationapp.utils.PhotoUtilsKt")
        every { encodePhotoToBase64(context, uri) } returns "encoded-photo"
        val write = stubUpdate("profilePicture", "encoded-photo")

        viewModel.updateProfilePicture(context, uri)
        verify(timeout = 2_000) { write.task.addOnFailureListener(any<OnFailureListener>()) }
        write.onSuccess!!.onSuccess(null)

        assertEquals("encoded-photo", viewModel.profilePicture)
        assertFalse(viewModel.isSaving)
    }

    /** Sets up deleting a profile that is on the waitlist of two lotteries. */
    private fun stubDelete(): Triple<FakeTask<QuerySnapshot>, WriteBatch, List<DocumentSnapshot>> {
        val query = mockk<Query>()
        val read = FakeTask<QuerySnapshot>()
        every { lotteriesRef.whereArrayContains("waitlist", profileId) } returns query
        every { query.get() } returns read.task

        val lotteries = List(2) {
            mockk<DocumentSnapshot> { every { reference } returns mockk() }
        }
        val batch = mockk<WriteBatch>(relaxed = true)
        every { db.batch() } returns batch
        return Triple(read, batch, lotteries)
    }

    /** Returns a mocked query result containing [documents]. */
    private fun snapshotOf(documents: List<DocumentSnapshot>): QuerySnapshot =
        mockk { every { this@mockk.documents } returns documents }

    @Test
    fun deleteProfile_success_removesFromWaitlistsAndDeletes() {
        loadProfile()
        val (read, batch, lotteries) = stubDelete()
        val commit = FakeTask<Void>()
        every { batch.commit() } returns commit.task

        viewModel.deleteProfile()
        read.onSuccess!!.onSuccess(snapshotOf(lotteries))
        assertFalse(viewModel.isDeleted)
        commit.onSuccess!!.onSuccess(null)

        val references = lotteries.map { it.reference }
        references.forEach { reference ->
            verify { batch.update(reference, "waitlist", any()) }
        }
        verify { batch.delete(document) }
        assertTrue(viewModel.isDeleted)
    }

    @Test
    fun deleteProfile_lookupFails_showsMessageAndKeepsProfile() {
        loadProfile()
        val (read, batch, _) = stubDelete()

        viewModel.deleteProfile()
        read.onFailure!!.onFailure(Exception("Network unavailable"))

        verify(exactly = 0) { batch.commit() }
        assertFalse(viewModel.isDeleted)
        assertFalse(viewModel.isSaving)
        assertNotNull(viewModel.message)
    }

    @Test
    fun deleteProfile_commitFails_showsMessageAndKeepsProfile() {
        loadProfile()
        val (read, batch, lotteries) = stubDelete()
        val commit = FakeTask<Void>()
        every { batch.commit() } returns commit.task

        viewModel.deleteProfile()
        read.onSuccess!!.onSuccess(snapshotOf(lotteries))
        commit.onFailure!!.onFailure(Exception("Network unavailable"))

        assertFalse(viewModel.isDeleted)
        assertFalse(viewModel.isSaving)
        assertNotNull(viewModel.message)
    }

    @Test
    fun clearMessage_removesMessage() {
        val read = FakeTask<DocumentSnapshot>()
        every { document.get() } returns read.task
        viewModel.load(profileId)
        read.onFailure!!.onFailure(Exception("Network unavailable"))

        viewModel.clearMessage()

        assertNull(viewModel.message)
    }
}
