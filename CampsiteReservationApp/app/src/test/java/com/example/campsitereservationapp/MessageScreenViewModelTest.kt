/**
 * Test for MessageScreenViewModel
 *
 * Tests:
 * - getName: returns the name of a profile by its ID, or empty text for an unknown ID
 * - sendMessage: saves a Message with the text, sender, and receiver to the Messages
 *   collection, then adds its ID to the chat's messageIDList
 * - start as entrant with no chat yet: looks up the chat with the other user as owner,
 *   then creates a new Chat with the right owner and entrant IDs
 * - start as owner with an existing chat: swaps the owner and entrant IDs, and uses the
 *   existing chat instead of creating one
 * - chat listener: fills messages from the chat's messageIDList, sorted by timestamp,
 *   and skips IDs with no matching message
 *
 * AI Reference: AI Use Logs - Jacob/Claude-Chat-Log_2026-11-25_P3-requirments.md
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Profile
import com.example.campsitereservationapp.ui.MessageScreenViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class MessageScreenViewModelTest {

    private val entrantId = "device-7f3a91c2"
    private val ownerId = "owner-device-22be0d14"

    private val firestore = MockFirestore()
    private val newChat = mockk<DocumentReference>(relaxed = true)     // from chatsRef.document()
    private val existingChat = mockk<DocumentReference>(relaxed = true) // found by the chat query
    private val profileDoc = mockk<DocumentReference>(relaxed = true)
    private val profileRead = MockTask<DocumentSnapshot>()
    private val chatQueryRead = MockTask<QuerySnapshot>()
    private lateinit var viewModel: MessageScreenViewModel

    @Before
    fun setUp() {
        firestore.install()
        every { newChat.id } returns "chat-new"
        every { firestore.collection("Chats").document() } returns newChat
        every { firestore.collection("Profiles").document(any()) } returns profileDoc
        every { profileDoc.get() } returns profileRead.task
        viewModel = MessageScreenViewModel()
    }

    @After
    fun tearDown() {
        firestore.uninstall()
    }

    /** Makes the chat query answer only when it asks for this owner and entrant. */
    private fun stubChatQuery(owner: String, entrant: String) {
        val byOwner = mockk<Query>()
        val byBoth = mockk<Query>()
        every { firestore.collection("Chats").whereEqualTo("ownerID", owner) } returns byOwner
        every { byOwner.whereEqualTo("entrantID", entrant) } returns byBoth
        every { byBoth.get() } returns chatQueryRead.task
    }

    private fun profileSnapshot(role: String) = mockk<DocumentSnapshot> {
        every { toObject(Profile::class.java) } returns Profile(name = "Someone").apply { this.role = role }
    }

    private fun chatQueryResult(found: DocumentReference?) = mockk<QuerySnapshot> {
        every { documents } returns
                if (found == null) emptyList() else listOf(mockk { every { reference } returns found })
    }

    // ---------- getName ----------

    @Test
    fun getName_returnsProfileName_orEmptyForUnknownId() {
        firestore.deliver("Profiles", Profile::class.java, listOf(
            Profile(name = "Alex Chen", deviceId = entrantId).apply { id = entrantId },
            Profile(name = "Sam Owner", deviceId = ownerId).apply { id = ownerId }
        ))

        assertEquals("Alex Chen", viewModel.getName(entrantId))
        assertEquals("Sam Owner", viewModel.getName(ownerId))
        assertEquals("", viewModel.getName("device-unknown"))
    }

    // ---------- sendMessage ----------

    @Test
    fun sendMessage_savesMessageAndAddsItToChat() {
        val messageDoc = mockk<DocumentReference>(relaxed = true)
        every { messageDoc.id } returns "message-1"
        every { firestore.collection("Messages").document() } returns messageDoc
        val sent = slot<Message>()

        viewModel.sendMessage(entrantId, ownerId, "Is the fire pit open?")

        verify { messageDoc.set(capture(sent)) }
        assertEquals("Is the fire pit open?", sent.captured.text)
        assertEquals(entrantId, sent.captured.senderID)
        assertEquals(ownerId, sent.captured.receiverID)
        verify { newChat.update("messageIDList", any(), *anyVararg()) }
    }

    // ---------- start ----------

    @Test
    fun start_asEntrant_noChatYet_createsChat() {
        stubChatQuery(owner = ownerId, entrant = entrantId)
        val created = slot<Chat>()

        viewModel.start(yourID = entrantId, otherID = ownerId)
        profileRead.succeed(profileSnapshot(role = "Entrant"))
        chatQueryRead.succeed(chatQueryResult(found = null))

        verify { newChat.set(capture(created)) }
        assertEquals(ownerId, created.captured.ownerID)
        assertEquals(entrantId, created.captured.entrantID)
        assertEquals("chat-new", created.captured.id)
        assertSame(newChat, viewModel.myChat)
    }

    @Test
    fun start_asOwner_existingChat_usesIt() {
        // you are the owner, so the IDs are swapped compared to the entrant case
        stubChatQuery(owner = ownerId, entrant = entrantId)

        viewModel.start(yourID = ownerId, otherID = entrantId)
        profileRead.succeed(profileSnapshot(role = "Owner"))
        chatQueryRead.succeed(chatQueryResult(found = existingChat))

        assertSame(existingChat, viewModel.myChat)
        verify(exactly = 0) { newChat.set(any()) }
    }

    // ---------- chat listener ----------

    @Test
    fun chatListener_fillsMessagesSortedByTime_skipsMissing() {
        val earlier = Message("Hi!", entrantId, ownerId, Timestamp(1_800_000_000, 0)).apply { id = "message-earlier" }
        val later = Message("Hello", ownerId, entrantId, Timestamp(1_800_000_600, 0)).apply { id = "message-later" }
        firestore.deliver("Messages", Message::class.java, listOf(later, earlier))

        val chatListener = slot<EventListener<DocumentSnapshot>>()
        every { existingChat.addSnapshotListener(capture(chatListener)) } returns mockk(relaxed = true)
        stubChatQuery(owner = ownerId, entrant = entrantId)

        viewModel.start(yourID = entrantId, otherID = ownerId)
        profileRead.succeed(profileSnapshot(role = "Entrant"))
        chatQueryRead.succeed(chatQueryResult(found = existingChat))

        // the chat lists the newer message first and one ID with no message
        val chat = Chat(ownerID = ownerId, entrantID = entrantId).apply {
            messageIDList = listOf("message-later", "message-earlier", "message-deleted")
        }
        chatListener.captured.onEvent(mockk { every { toObject(Chat::class.java) } returns chat }, null)

        assertEquals(listOf(earlier, later), viewModel.messages)
    }
}