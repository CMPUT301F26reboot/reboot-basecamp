/*
 * AI use:
 * - Q: How do I test ViewModels that connect to Firebase when they're created?
 *   A: Plain JUnit for the models; MockK to mock Firebase.firestore for the ViewModels.
 * - Some of the mock tests were written using Claude's output: the shared MockFirestore.kt helper
 *   and the ViewModel tests that use it.
 * - AI code assist in Android Studio was used while coding, mainly tab autocomplete.
 */
package com.example.campsitereservationapp

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import java.util.Calendar
import java.util.Date

/**
 * Mocked Firestore shared by the ViewModel tests. Call [install] in `@Before` before creating a
 * ViewModel, push test data in with [deliver], and call [uninstall] in `@After`.
 */
class MockFirestore {

    private val db = mockk<FirebaseFirestore>(relaxed = true)
    private val collections = mutableMapOf<String, CollectionReference>()
    private val listeners = mutableMapOf<String, EventListener<QuerySnapshot>>()

    /** Makes `Firebase.firestore` return the mocked database. */
    fun install() {
        listOf("Listings", "Lotteries", "Profiles", "Reservations", "LogEntries", "Chats").forEach { name ->
            val ref = mockk<CollectionReference>(relaxed = true)
            every { ref.addSnapshotListener(any<EventListener<QuerySnapshot>>()) } answers {
                listeners[name] = firstArg()
                mockk(relaxed = true)
            }
            every { db.collection(name) } returns ref
            collections[name] = ref
        }
        mockkStatic("com.google.firebase.firestore.FirestoreKt")
        every { Firebase.firestore } returns db
    }

    /** Removes all mocks. */
    fun uninstall() {
        unmockkAll()
    }

    /** The mocked collection the ViewModel writes to, e.g. "Reservations". */
    fun collection(name: String): CollectionReference = collections.getValue(name)

    /** Sends [items] to the ViewModel as if Firestore's listener on [collection] had fired. */
    fun <T : Any> deliver(collection: String, type: Class<T>, items: List<T>) {
        val docs = items.map { item -> mockk<DocumentSnapshot> { every { toObject(type) } returns item } }
        val snapshot = mockk<QuerySnapshot> { every { documents } returns docs }
        listeners.getValue(collection).onEvent(snapshot, null)
    }
}

/** A mocked Firestore write whose result the test decides by calling [succeed] or [fail]. */
class MockTask<T> {

    val task = mockk<Task<T>>()
    private var onSuccess: OnSuccessListener<T>? = null
    private var onFailure: OnFailureListener? = null

    init {
        every { task.addOnSuccessListener(any<OnSuccessListener<T>>()) } answers { onSuccess = firstArg(); task }
        every { task.addOnFailureListener(any<OnFailureListener>()) } answers { onFailure = firstArg(); task }
    }

    /** Completes the write. [result] is null for writes like `update()` that return nothing. */
    fun succeed(result: T?) = onSuccess!!.onSuccess(result)

    fun fail() = onFailure!!.onFailure(Exception("Network unavailable"))
}

/** Midnight on the given day. [month] is 1-based. */
fun day(year: Int, month: Int, day: Int): Date {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(year, month - 1, day)
    return cal.time
}

/** A date [days] from now (negative for the past), for tests that compare against today. */
fun daysFromNow(days: Int): Date {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, days)
    return cal.time
}
