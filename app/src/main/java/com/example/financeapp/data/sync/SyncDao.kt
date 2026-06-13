package com.example.financeapp.data.sync

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SyncDao {

    @Insert
    suspend fun inserirOperacao(operacao: OperacaoPendente)

    @Query("SELECT * FROM operacoes_pendentes ORDER BY criadoEm ASC")
    suspend fun obterPendentes(): List<OperacaoPendente>

    @Query("DELETE FROM operacoes_pendentes WHERE id = :id")
    suspend fun removerOperacao(id: Long)

    @Query("DELETE FROM operacoes_pendentes")
    suspend fun limparTudo()

    @Query("SELECT COUNT(*) FROM operacoes_pendentes")
    suspend fun contarPendentes(): Int
}

