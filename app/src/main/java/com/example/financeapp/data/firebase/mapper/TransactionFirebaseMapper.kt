package com.example.financeapp.data.firebase.mapper

import com.example.financeapp.data.firebase.dto.TransactionFirebaseDto
import com.example.financeapp.domain.model.Transaction
import com.example.financeapp.domain.model.TransactionType
import java.time.LocalDateTime

fun Transaction.toFirebaseDto(): TransactionFirebaseDto {

    return TransactionFirebaseDto(

        id = id,

        description = description,

        amount = amount.toDouble(),

        date = date.toString(),

        type = type.name
    )
}

fun TransactionFirebaseDto.toDomain(): Transaction {

    return Transaction(

        id = id,

        description = description,

        amount = amount.toBigDecimal(),

        date = LocalDateTime.parse(date),

        type = TransactionType.valueOf(type)
    )
}