package com.example.financeapp.di

import com.example.financeapp.data.auth.AuthRepositoryImpl
import com.example.financeapp.data.auth.TokenManager
import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.repository.TransactionRepositoryRemoteImpl
import com.example.financeapp.data.remote.repository.FinanceRepositoryImpl
import com.example.financeapp.data.remote.repository.PixRepositoryImpl
import com.example.financeapp.data.remote.repository.TransferRepositoryImpl
import com.example.financeapp.data.remote.repository.UserRepositoryImpl
import com.example.financeapp.domain.repository.AuthRepository
import com.example.financeapp.domain.repository.FinanceRepository
import com.example.financeapp.domain.repository.PixRepository
import com.example.financeapp.domain.repository.TransactionRepository
import com.example.financeapp.domain.repository.TransferRepository
import com.example.financeapp.domain.repository.UserRepository
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
import com.example.financeapp.ui.screens.investimento.InvestimentoViewModel
import com.example.financeapp.ui.screens.login.LoginViewModel
import com.example.financeapp.ui.screens.deposito.DepositoViewModel
import com.example.financeapp.ui.screens.extrato.ExtratoViewModel
import com.example.financeapp.ui.screens.pagamento.PagamentoViewModel
import com.example.financeapp.ui.screens.relatorios.RelatorioViewModel
import com.example.financeapp.ui.screens.cartoes.CartoesViewModel
import com.example.financeapp.ui.screens.perfil.PerfilViewModel
import com.example.financeapp.ui.screens.pix.MinhasChavesViewModel
import com.example.financeapp.ui.screens.pix.PixEnviarViewModel
import com.example.financeapp.ui.screens.pix.PixHistoricoViewModel
import com.example.financeapp.ui.screens.pix.PixHubViewModel
import com.example.financeapp.ui.screens.pix.PixReceberViewModel
import com.example.financeapp.ui.screens.privacidade.PrivacidadeViewModel
import com.example.financeapp.ui.screens.registro.RegistroViewModel
import com.example.financeapp.ui.screens.seguranca.SegurancaViewModel
import com.example.financeapp.ui.screens.transferencia.FavorecidosViewModel
import com.example.financeapp.ui.screens.transferencia.TransferenciaEnviarViewModel
import com.example.financeapp.ui.screens.transferencia.TransferenciaHubViewModel
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

    // === Repositório de Usuário ===
    single<UserRepository> { UserRepositoryImpl(get(), get(), get()) }

    // === Repositório Pix ===
    single<PixRepository> { PixRepositoryImpl(get(), get(), get()) }

    // === Repositório Transferência ===
    single<TransferRepository> { TransferRepositoryImpl(get(), get(), get()) }

    // === Repositório Finance ===
    single<FinanceRepository> { FinanceRepositoryImpl(get(), get(), get()) }

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
    viewModel { PerfilViewModel(get()) }
    viewModel { SegurancaViewModel(get()) }
    viewModel { PrivacidadeViewModel(get()) }
    viewModel { PixHubViewModel(get()) }
    viewModel { PixEnviarViewModel(get()) }
    viewModel { PixReceberViewModel(get()) }
    viewModel { MinhasChavesViewModel(get()) }
    viewModel { PixHistoricoViewModel(get()) }
    viewModel { TransferenciaHubViewModel(get()) }
    viewModel { TransferenciaEnviarViewModel(get()) }
    viewModel { FavorecidosViewModel(get()) }
    viewModel { DepositoViewModel(get()) }
    viewModel { PagamentoViewModel(get()) }
    viewModel { CartoesViewModel(get()) }
    viewModel { InvestimentoViewModel(get()) }
    viewModel { ExtratoViewModel(get()) }
    viewModel { RelatorioViewModel(get()) }
}