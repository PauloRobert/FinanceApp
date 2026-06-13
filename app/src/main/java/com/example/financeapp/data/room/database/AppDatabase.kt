package com.example.financeapp.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.financeapp.data.room.dao.TransactionDao
import com.example.financeapp.data.room.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}

