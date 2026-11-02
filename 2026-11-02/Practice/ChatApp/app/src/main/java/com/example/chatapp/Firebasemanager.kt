package com.example.chatapp

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.UUID

data class User(
    val uid: String = "",
    val nickname: String = ""
)

data class Chatroom(
    val chatroomId: String = "",
    val user1Nickname: String = "",
    val user2Nickname: String = "",
    val user1Uid: String = "",
    val user2Uid: String = ""
)

data class ChatMessage(
    val chatId: String = "",
    val chatroomId: String = "",
    val userId: String = "",
    val senderNickname: String = "",
    val msg: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

object FirebaseManager {

    val db: FirebaseFirestore
        get() = FirebaseFirestore.getInstance()

    fun createUser(nickname: String, onResult: (User?) -> Unit) {
        val uid = UUID.randomUUID().toString()
        val user = User(uid = uid, nickname = nickname)
        db.collection("users").document(uid).set(user)
            .addOnSuccessListener { onResult(user) }
            .addOnFailureListener { onResult(null) }
    }

    fun getUsers(onResult: (List<User>) -> Unit) {
        db.collection("users").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                onResult(users)
            } else {
                onResult(emptyList())
            }
        }
    }

    fun getOrCreateChatroom(user1: User, user2: User, onResult: (Chatroom?) -> Unit) {
        db.collection("chatrooms").get().addOnSuccessListener { snapshot ->
            val existing = snapshot.documents.mapNotNull { it.toObject(Chatroom::class.java) }.find {
                (it.user1Uid == user1.uid && it.user2Uid == user2.uid) ||
                        (it.user1Uid == user2.uid && it.user2Uid == user1.uid)
            }

            if (existing != null) {
                onResult(existing)
            } else {
                val chatroomId = UUID.randomUUID().toString()
                val newChatroom = Chatroom(
                    chatroomId = chatroomId,
                    user1Nickname = user1.nickname,
                    user2Nickname = user2.nickname,
                    user1Uid = user1.uid,
                    user2Uid = user2.uid
                )
                db.collection("chatrooms").document(chatroomId).set(newChatroom)
                    .addOnSuccessListener { onResult(newChatroom) }
                    .addOnFailureListener { onResult(null) }
            }
        }.addOnFailureListener {
            onResult(null)
        }
    }

    fun sendMessage(chatroom: Chatroom, sender: User, text: String, onComplete: () -> Unit = {}) {
        val chatId = UUID.randomUUID().toString()
        val message = ChatMessage(
            chatId = chatId,
            chatroomId = chatroom.chatroomId,
            userId = sender.uid,
            senderNickname = sender.nickname,
            msg = text,
            timestamp = System.currentTimeMillis()
        )
        db.collection("chats").document(chatId).set(message)
            .addOnCompleteListener { onComplete() }
    }

    fun listenToMessages(chatroomId: String, onUpdate: (List<ChatMessage>) -> Unit): ListenerRegistration {
        return db.collection("chats")
            .whereEqualTo("chatroomId", chatroomId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { it.toObject(ChatMessage::class.java) }
                    onUpdate(messages)
                } else {
                    onUpdate(emptyList())
                }
            }
    }
}
