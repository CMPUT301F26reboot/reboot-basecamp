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

//                loadMessages()
            }





    }

    fun sendMessage(senderID: String, receiverID: String, text: String){
        var myMessage = messagesRef.document()
        val newMessage = Message(text, senderID, receiverID, Timestamp.now())
        myMessage.set(newMessage)
        myChat.update("messageIDList", FieldValue.arrayUnion(myMessage.id))
    }

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

    private fun loadMessages(){
        messages.clear()
        myChat.get().addOnSuccessListener { snapshot ->
            var tempChat = snapshot?.toObject(Chat::class.java)
            if (tempChat != null) {
                for (messageID in tempChat.messageIDList) {
                    val message = getMessage(messageID)
                    if (message.id.isNotEmpty()) {
                        messages.add(message)
                    }
                }
                messages.sortBy { it.timestamp }
            }
        }
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



    private fun getMessageID(senderID: String, receiverID: String, text: String, timestamp: Timestamp): String{
        var answer = ""
        val queryResult = messagesRef
            .whereEqualTo("senderID", senderID)
            .whereEqualTo("senderID", senderID)
            .whereEqualTo("text", text)
            .whereEqualTo("timestamp", timestamp)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val temp = querySnapshot.documents.first().toObject(Message::class.java)?.id
                if(temp != null){
                    answer = temp
                }
            }
        return answer
    }
}