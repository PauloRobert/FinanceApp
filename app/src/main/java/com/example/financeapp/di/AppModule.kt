package com.example.financeapp.di

import com.example.financeapp.data.firebase.datasource.FirestoreDataSource
import com.example.financeapp.data.repository.TransactionRepositoryFirebaseImpl
import com.example.financeapp.domain.repository.TransactionRepository
import com.example.financeapp.domain.usecase.DeleteTransactionUseCase
import com.example.financeapp.domain.usecase.GetTransactionsUseCase
import com.example.financeapp.domain.usecase.InsertTransactionsUseCase
import com.example.financeapp.domain.usecase.UpdateTransactionUseCase
import com.example.financeapp.ui.screens.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    //Repository
    single {
        FirestoreDataSource()
    }
    single<TransactionRepository> {
        TransactionRepositoryFirebaseImpl(get())
    }

    //UseCase
    factory {
        GetTransactionsUseCase(get())
    }

    factory { InsertTransactionsUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }

    //VIewModel
    viewModel {
        HomeViewModel(
            get(),
            get(),
            get(),
            get()
        )
    }
}