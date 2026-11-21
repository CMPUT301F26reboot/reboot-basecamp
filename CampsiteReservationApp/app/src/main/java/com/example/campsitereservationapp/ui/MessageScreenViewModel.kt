package com.example.campsitereservationapp.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Chat
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.LogEntry
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Documents.Message
import com.example.campsitereservationapp.AppUI.DatabaseComponents.Models.DatabaseModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

/**
 * View Model for the message screen
 *
 * Finds the appropriate chat or creates one if one does not exist
 * Holds a list of messages is [messages]
 * Provides ability to send a message and find name of a chatter
 * Automatically updates [messages] when a message is sent
 */
class MessageScreenViewModel: DatabaseModel() {

    var myChat = chatsRef.document()
    var messages = mutableStateListOf<Message>()
    

    /**
     * sets up the viewmodel
     *
     * Finds if you are the owner or the other is
     * Sets myChat to the document reference of the chat between the owner and entrant if it exists,
     * otherwise create a new reference and add it to the database
     * Loads the messages in the chat into [messages]
     * Adds a listener on myChat to update [messages]
     */
    fun start(yourID: String, otherID: String){
        var ownerID = otherID
        var entrantID = yourID
        if(idIsOwner(yourID)){
            ownerID = yourID
            entrantID = otherID
        }
        chatsRef
            .whereEqualTo("ownerID", ownerID)
            .whereEqualTo("entrantID", entrantID)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val temp = querySnapshot.documents.firstOrNull()?.reference
                if(temp != null){
                    myChat = temp
                }else{
                    var tempChat = Chat(ownerID, entrantID)
                    tempChat.id = myChat.id
                    myChat.set(tempChat)
                }

                addChatListener()

            }





    }

    /**
     * Sends a message
     *
     * Creates a message and saves it to the Messages collection,
     * then adds the id of that message to the messageIDList of the chat
     * triggering a recomposition
     *
     * @param [senderID] is the document id for the sender's profile document
     * @param [receiverID] is the document id for the receiver's profile document
     * @param [text] is the text of the message
     */
    fun sendMessage(senderID: String, receiverID: String, text: String){
        var myMessage = messagesRef.document()
        val newMessage = Message(text, senderID, receiverID, Timestamp.now())
        myMessage.set(newMessage)
        myChat.update("messageIDList", FieldValue.arrayUnion(myMessage.id))
    }

    /**
     * Gets the name of a profile based on a profile id
     *
     * @param [profileID] document id of the profile you want to get the name of
     */
    fun getName(profileID: String): String{
        for(profile in _profiles){
            if(profile.id == profileID){
                return profile.name
            }
        }
        return ""
    }
    private fun idIsOwner(ID: String): Boolean{
        for(profile in _profiles){
            if(profile.id == ID && profile.role == "Owner"){
                return true
            }
        }
        return false
    }


    private fun getMessage(messageID: String): Message{
        for(message in _messages){
            if(message.id == messageID){
                return message
            }
        }
        return Message()
    }


    private fun addChatListener(){
        myChat.addSnapshotListener { snapshot, error ->
            if(error != null){
                return@addSnapshotListener
            }

            messages.clear()
            var tempChat = snapshot?.toObject(Chat::class.java)
            if(tempChat != null){
                for(messageID in tempChat.messageIDList){
                    val message = getMessage(messageID)
                    if(message.id.isNotEmpty()){
                        messages.add(message)
                    }
                }
                messages.sortBy { it.timestamp }
            }
        }
    }
    
}