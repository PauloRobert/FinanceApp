package com.example.financeapp.data.room.mapper

import com.example.financeapp.data.room.entity.TransactionEntity
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import java.time.LocalDateTime

// Converte entidade Room para modelo de domínio
fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        description = descricao,
        amount = valor.toBigDecimal(),
        date = LocalDateTime.parse(data),
        type = TransactionType.valueOf(tipo)
    )
}

// Converte modelo de domínio para entidade Room
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        descricao = description,
        valor = amount.toDouble(),
        data = date.toString(),
        tipo = type.name
    )
}

