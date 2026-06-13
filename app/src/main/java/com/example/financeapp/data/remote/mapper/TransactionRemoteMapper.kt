package com.example.financeapp.data.remote.mapper

import com.example.financeapp.data.remote.dto.TransactionRemoteDto
import com.example.financeapp.data.remote.dto.TransactionRequest
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import java.time.LocalDateTime

// Converte DTO remoto para modelo de domínio
fun TransactionRemoteDto.toDomain(): Transaction {
    return Transaction(
        id = id,
        description = description,
        amount = amount.toBigDecimal(),
        date = LocalDateTime.parse(date),
        type = TransactionType.valueOf(type)
    )
}

// Converte modelo de domínio para DTO de requisição
fun Transaction.toRequest(): TransactionRequest {
    return TransactionRequest(
        description = description,
        amount = amount.toDouble(),
        date = date.toString(),
        type = type.name
    )
}

