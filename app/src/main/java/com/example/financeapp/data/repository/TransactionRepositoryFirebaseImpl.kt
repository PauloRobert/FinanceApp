package com.example.financeapp.data.repository

import com.example.financeapp.data.firebase.datasource.FirestoreDataSource
import com.example.financeapp.data.firebase.dto.TransactionFirebaseDto
import com.example.financeapp.data.firebase.mapper.toDomain
import com.example.financeapp.data.firebase.mapper.toFirebaseDto
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class TransactionRepositoryFirebaseImpl(

    private val dataSource: FirestoreDataSource

) : TransactionRepository {
    private val transactions =
        MutableStateFlow<List<Transaction>>(emptyList())

    init {
        observeTransactions()
    }

    private fun observeTransactions() {
        dataSource.transactionsCollection.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener

            val list = snapshot.documents.mapNotNull { document ->
                document
                    .toObject(TransactionFirebaseDto::class.java)
                    ?.copy(id = document.id)
                    ?.toDomain()
            }
            transactions.value = list
        }
    }

    override fun getTransactions(): Flow<List<Transaction>> {
        return transactions
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        val document = dataSource.transactionsCollection.document()
        val dto = transaction.copy(id = document.id).toFirebaseDto()
        document.set(dto).await()
    }

    override suspend fun deleteTransacrion(id: String) {
        dataSource.transactionsCollection.document(id).delete().await()
    }

    override suspend fun updateTransacrion(transaction: Transaction) {
        dataSource
            .transactionsCollection
            .document(transaction.id)
            .set(transaction.toFirebaseDto())
            .await()
    }
}