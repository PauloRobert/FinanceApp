package com.example.financeapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financeapp.data.room.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transacoes ORDER BY data DESC")
    fun obterTodas(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transacoes WHERE id = :id")
    suspend fun obterPorId(id: String): TransactionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(entidade: TransactionEntity)

    @Query("DELETE FROM transacoes WHERE id = :id")
    suspend fun deletar(id: String)

    @Update
    suspend fun atualizar(entidade: TransactionEntity)
}

