package com.example.financeapp.data.firebase.datasource

import com.google.firebase.firestore.FirebaseFirestore

class FirestoreDataSource {
    private val firestore =
        FirebaseFirestore.getInstance()

    val transactionsCollection =
        firestore.collection("transactions")
}