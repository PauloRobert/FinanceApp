package com.example.financeapp.di

import com.example.financeapp.data.auth.AuthRepositoryImpl
import com.example.financeapp.data.auth.TokenManager
import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.repository.TransactionRepositoryRemoteImpl
import com.example.financeapp.domain.repository.AuthRepository
import com.example.financeapp.domain.repository.TransactionRepository
import com.example.financeapp.domain.usecase.DeleteTransactionUseCase
import com.example.financeapp.domain.usecase.GetTransactionsUseCase
import com.example.financeapp.domain.usecase.InsertTransactionsUseCase
import com.example.financeapp.domain.usecase.LoginUseCase
import com.example.financeapp.domain.usecase.LogoutUseCase
import com.example.financeapp.domain.usecase.RegistrarUseCase
import com.example.financeapp.domain.usecase.UpdateTransactionUseCase
import com.example.financeapp.domain.usecase.VerificarSessaoUseCase
import com.example.financeapp.ui.screens.configuracoes.ConfiguracoesViewModel
import com.example.financeapp.ui.screens.home.HomeViewModel
import com.example.financeapp.ui.screens.login.LoginViewModel
import com.example.financeapp.ui.screens.registro.RegistroViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // === Retrofit ===
    single { AuthInterceptor() }
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(logging)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl("https://finance-api-rest-3rp8u.ondigitalocean.app/api/v1/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    single { get<Retrofit>().create(TransactionApi::class.java) }

    // === Autenticação ===
    single { TokenManager(androidContext()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get()) }

    // === Repositório de Transações (API REST) ===
    single<TransactionRepository> { TransactionRepositoryRemoteImpl(get(), get(), get()) }

    // === Use Cases de Auth ===
    factory { LoginUseCase(get()) }
    factory { RegistrarUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { VerificarSessaoUseCase(get()) }

    // === Use Cases de Transações ===
    factory { GetTransactionsUseCase(get()) }
    factory { InsertTransactionsUseCase(get()) }
    factory { UpdateTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }

    // === ViewModels ===
    viewModel { HomeViewModel(get(), get()) }
    viewModel { ConfiguracoesViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegistroViewModel(get()) }
}