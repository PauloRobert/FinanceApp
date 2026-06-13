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

    // Fluxo interno de transações observadas do Firestore
    private val transacoes = MutableStateFlow<List<Transaction>>(emptyList())

    init {
        observarTransacoes()
    }

    private fun observarTransacoes() {
        dataSource.transactionsCollection.addSnapshotListener { snapshot, _ ->
            if (snapshot == null) return@addSnapshotListener

            val lista = snapshot.documents.mapNotNull { documento ->
                documento
                    .toObject(TransactionFirebaseDto::class.java)
                    ?.copy(id = documento.id)
                    ?.toDomain()
            }
            transacoes.value = lista
        }
    }

    override fun obterTransacoes(): Flow<List<Transaction>> {
        return transacoes
    }

    override suspend fun inserirTransacao(transacao: Transaction) {
        val documento = dataSource.transactionsCollection.document()
        val dto = transacao.copy(id = documento.id).toFirebaseDto()
        documento.set(dto).await()
    }

    override suspend fun deletarTransacao(id: String) {
        dataSource.transactionsCollection.document(id).delete().await()
    }

    override suspend fun atualizarTransacao(transacao: Transaction) {
        dataSource
            .transactionsCollection
            .document(transacao.id)
            .set(transacao.toFirebaseDto())
            .await()
    }
}