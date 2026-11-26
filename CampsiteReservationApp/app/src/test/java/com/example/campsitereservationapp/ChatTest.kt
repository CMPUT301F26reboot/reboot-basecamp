/** Test file for Chat
 * AI use:
 * - What important aspects of tests should I cover when I am testing the file for chat?
 * Output : - stores the owner and entrant IDs;
 *          - starts with no messages and an empty ID;
 *          - defaults every field to empty when created with no arguments, which is how Firestore builds it;
 *          - keeps messageIDList in the order messages were added
 */
package com.example.campsitereservationapp

import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for [Chat], the conversation between one owner and one entrant. The chat only stores
 * message IDs; the messages themselves live in their own collection.
 */
class ChatTest {

    private val ownerId = "owner-device-22be0d14"
    private val entrantId = "device-7f3a91c2"

    @Test
    fun chat_shouldStoreOwnerAndEntrant() {
        val chat = Chat(ownerID = ownerId, entrantID = entrantId)

        assertEquals(ownerId, chat.ownerID)
        assertEquals(entrantId, chat.entrantID)
    }

    @Test
    fun chat_newChat_shouldHaveNoMessagesOrId() {
        val chat = Chat(ownerID = ownerId, entrantID = entrantId)

        assertTrue(chat.messageIDList.isEmpty())
        assertEquals("", chat.id)
    }

    @Test
    fun chat_noArguments_shouldUseEmptyDefaults() {
        // Firestore builds a Chat with no arguments before filling in its fields.
        val chat = Chat()

        assertEquals("", chat.ownerID)
        assertEquals("", chat.entrantID)
        assertTrue(chat.messageIDList.isEmpty())
    }

    @Test
    fun chat_messageIDList_shouldKeepMessagesInOrderAdded() {
        val chat = Chat(ownerID = ownerId, entrantID = entrantId)

        chat.messageIDList = chat.messageIDList + "message-1"
        chat.messageIDList = chat.messageIDList + "message-2"

        assertEquals(listOf("message-1", "message-2"), chat.messageIDList)
    }
}
