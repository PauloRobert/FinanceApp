package com.example.financeapp.di

import androidx.room.Room
import com.example.financeapp.data.config.DataSourceConfigRepositoryImpl
import com.example.financeapp.data.connectivity.ConnectivityObserver
import com.example.financeapp.data.firebase.datasource.FirestoreDataSource
import com.example.financeapp.data.provider.RepositoryProvider
import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.repository.TransactionRepositoryRemoteImpl
import com.example.financeapp.data.repository.TransactionRepositoryFirebaseImpl
import com.example.financeapp.data.room.database.AppDatabase
import com.example.financeapp.data.room.repository.TransactionRepositoryRoomImpl
import com.example.financeapp.data.sync.SyncManager
import com.example.financeapp.domain.repository.DataSourceConfigRepository
import com.example.financeapp.domain.repository.TransactionRepository
import com.example.financeapp.domain.usecase.DeleteTransactionUseCase
import com.example.financeapp.domain.usecase.GetTransactionsUseCase
import com.example.financeapp.domain.usecase.InsertTransactionsUseCase
import com.example.financeapp.domain.usecase.UpdateTransactionUseCase
import com.example.financeapp.ui.screens.configuracoes.ConfiguracoesViewModel
import com.example.financeapp.ui.screens.home.HomeViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {

    // === Configuração ===
    single<DataSourceConfigRepository> {
        DataSourceConfigRepositoryImpl(androidContext())
    }

    // === Firebase ===
    single { FirestoreDataSource() }
    single<TransactionRepository>(named("firebase")) {
        TransactionRepositoryFirebaseImpl(get())
    }

    // === Room ===
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "finance_database"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<AppDatabase>().transactionDao() }
    single { get<AppDatabase>().syncDao() }
    single<TransactionRepository>(named("room")) {
        TransactionRepositoryRoomImpl(get())
    }

    // === Retrofit ===
    single { AuthInterceptor() }
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(logging)
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
    single<TransactionRepository>(named("remote")) {
        TransactionRepositoryRemoteImpl(get(), get())
    }

    // === Sync (infraestrutura interna) ===
    single { ConnectivityObserver(androidContext()) }
    single {
        SyncManager(
            syncDao = get(),
            transactionDao = get(),
            connectivityObserver = get(),
            configRepositorio = get(),
            repositorioRemote = get(named("remote")),
            repositorioFirebase = get(named("firebase"))
        )
    }

    // === Provider ===
    single {
        RepositoryProvider(
            configRepositorio = get(),
            repositorioRoom = get(named("room")),
            repositorioRemote = get(named("remote")),
            repositorioFirebase = get(named("firebase"))
        )
    }

    // === UseCases ===
    factory { GetTransactionsUseCase(get<RepositoryProvider>().obterRepositorioAtivo()) }
    factory { InsertTransactionsUseCase(get<RepositoryProvider>().obterRepositorioAtivo()) }
    factory { UpdateTransactionUseCase(get<RepositoryProvider>().obterRepositorioAtivo()) }
    factory { DeleteTransactionUseCase(get<RepositoryProvider>().obterRepositorioAtivo()) }

    // === ViewModels ===
    viewModel { HomeViewModel(get()) }
    viewModel { ConfiguracoesViewModel(get()) }
}