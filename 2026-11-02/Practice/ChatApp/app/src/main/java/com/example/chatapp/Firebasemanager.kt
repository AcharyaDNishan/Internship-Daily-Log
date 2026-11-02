package com.example.chatapp

import com.google.firebase.firestore.FirebaseFirestore

object FirebaseManager {

    val db: FirebaseFirestore =
        FirebaseFirestore.getInstance()
}