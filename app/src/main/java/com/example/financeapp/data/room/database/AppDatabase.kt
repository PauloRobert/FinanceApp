package com.example.financeapp.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeapp.data.room.dao.TransactionDao
import com.example.financeapp.data.room.entity.TransactionEntity
import com.example.financeapp.data.sync.OperacaoPendente
import com.example.financeapp.data.sync.SyncDao

@Database(
    entities = [TransactionEntity::class, OperacaoPendente::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun syncDao(): SyncDao
}
