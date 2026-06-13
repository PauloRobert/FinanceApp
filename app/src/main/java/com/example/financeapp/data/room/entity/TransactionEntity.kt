package com.example.financeapp.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidade Room que representa uma transação no banco local
@Entity(tableName = "transacoes")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val descricao: String,
    val valor: Double,
    val data: String, // ISO 8601
    val tipo: String  // INCOME ou EXPENSE
)

