package com.example.financeapp.data.sync

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tipos de operação pendente
enum class TipoOperacao {
    INSERIR,
    ATUALIZAR,
    DELETAR
}

// Entidade que armazena operações pendentes de sincronização
@Entity(tableName = "operacoes_pendentes")
data class OperacaoPendente(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val transacaoId: String,
    val tipo: String, // INSERIR, ATUALIZAR, DELETAR
    val criadoEm: Long = System.currentTimeMillis()
)

