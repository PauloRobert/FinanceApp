package com.example.financeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.financeapp.domain.usecase.VerificarSessaoUseCase
import com.example.financeapp.ui.screens.configuracoes.ConfiguracoesScreen
import com.example.financeapp.ui.screens.cartoes.CartoesScreen
import com.example.financeapp.ui.screens.deposito.DepositoScreen
import com.example.financeapp.ui.screens.home.HomeScreen
import com.example.financeapp.ui.screens.investimento.InvestimentoScreen
import com.example.financeapp.ui.screens.login.LoginScreen
import com.example.financeapp.ui.screens.pagamento.PagamentoScreen
import com.example.financeapp.ui.screens.perfil.PerfilScreen
import com.example.financeapp.ui.screens.pix.MinhasChavesScreen
import com.example.financeapp.ui.screens.pix.PixEnviarScreen
import com.example.financeapp.ui.screens.pix.PixHistoricoScreen
import com.example.financeapp.ui.screens.pix.PixHubScreen
import com.example.financeapp.ui.screens.pix.PixReceberScreen
import com.example.financeapp.ui.screens.privacidade.PrivacidadeScreen
import com.example.financeapp.ui.screens.registro.RegistroScreen
import com.example.financeapp.ui.screens.seguranca.SegurancaScreen
import com.example.financeapp.ui.screens.transferencia.FavorecidosScreen
import com.example.financeapp.ui.screens.transferencia.TransferenciaEnviarScreen
import com.example.financeapp.ui.screens.transferencia.TransferenciaHubScreen
import org.koin.compose.koinInject

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val verificarSessao: VerificarSessaoUseCase = koinInject()
    val estaLogado by verificarSessao().collectAsState(initial = false)

    NavHost(
        navController = navController,
        startDestination = Rotas.Login.rota
    ) {
        composable(Rotas.Login.rota) {
            LaunchedEffect(estaLogado) {
                if (estaLogado) {
                    navController.navigate(Rotas.Home.rota) {
                        popUpTo(Rotas.Login.rota) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                aoNavegarParaRegistro = {
                    navController.navigate(Rotas.Registro.rota)
                },
                aoLoginRealizado = {
                    navController.navigate(Rotas.Home.rota) {
                        popUpTo(Rotas.Login.rota) { inclusive = true }
                    }
                }
            )
        }
        composable(Rotas.Registro.rota) {
            RegistroScreen(
                aoVoltar = { navController.popBackStack() },
                aoRegistroRealizado = { navController.popBackStack() }
            )
        }
        composable(Rotas.Home.rota) {
            HomeScreen(
                aoAbrirConfiguracoes = { navController.navigate(Rotas.Configuracoes.rota) },
                aoAbrirPix = { navController.navigate(Rotas.PixHub.rota) },
                aoAbrirTransferencia = { navController.navigate(Rotas.TransferenciaHub.rota) },
                aoAbrirDeposito = { navController.navigate(Rotas.Deposito.rota) },
                aoAbrirPagamento = { navController.navigate(Rotas.Pagamento.rota) },
                aoAbrirCartoes = { navController.navigate(Rotas.Cartoes.rota) },
                aoAbrirInvestimento = { navController.navigate(Rotas.Investimento.rota) }
            )
        }
        composable(Rotas.Configuracoes.rota) {
            ConfiguracoesScreen(
                aoVoltar = { navController.popBackStack() },
                aoLogout = {
                    navController.navigate(Rotas.Login.rota) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                aoAbrirPerfil = {
                    navController.navigate(Rotas.Perfil.rota)
                },
                aoAbrirSeguranca = {
                    navController.navigate(Rotas.Seguranca.rota)
                },
                aoAbrirPrivacidade = {
                    navController.navigate(Rotas.Privacidade.rota)
                }
            )
        }
        composable(Rotas.Perfil.rota) {
            PerfilScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.Seguranca.rota) {
            SegurancaScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.Privacidade.rota) {
            PrivacidadeScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.PixHub.rota) {
            PixHubScreen(
                aoVoltar = { navController.popBackStack() },
                aoEnviarPix = { navController.navigate(Rotas.PixEnviar.rota) },
                aoReceberPix = { navController.navigate(Rotas.PixReceber.rota) },
                aoMinhasChaves = { navController.navigate(Rotas.PixChaves.rota) },
                aoExtratoPix = { navController.navigate(Rotas.PixHistorico.rota) },
                aoEnviarParaFavorito = { chave ->
                    navController.navigate(Rotas.PixEnviarFavorito.criarRota(chave))
                },
                aoEnviarComChave = { chave ->
                    navController.navigate(Rotas.PixEnviarFavorito.criarRota(chave))
                }
            )
        }
        composable(Rotas.PixEnviar.rota) {
            PixEnviarScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(
            Rotas.PixEnviarFavorito.rota,
            arguments = listOf(navArgument("chave") { type = NavType.StringType })
        ) { backStackEntry ->
            val chave = backStackEntry.arguments?.getString("chave")
            PixEnviarScreen(
                aoVoltar = { navController.popBackStack() },
                chaveInicial = chave
            )
        }
        composable(Rotas.PixReceber.rota) {
            PixReceberScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.PixChaves.rota) {
            MinhasChavesScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.PixHistorico.rota) {
            PixHistoricoScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.TransferenciaHub.rota) {
            TransferenciaHubScreen(
                aoVoltar = { navController.popBackStack() },
                aoTransferir = { navController.navigate(Rotas.TransferenciaEnviar.rota) },
                aoFavorecidos = { navController.navigate(Rotas.TransferenciaFavorecidos.rota) },
                aoTransferirParaFavorecido = { favId ->
                    navController.navigate(Rotas.TransferenciaEnviarFavorecido.criarRota(favId))
                }
            )
        }
        composable(Rotas.TransferenciaEnviar.rota) {
            TransferenciaEnviarScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(
            Rotas.TransferenciaEnviarFavorecido.rota,
            arguments = listOf(navArgument("favId") { type = NavType.StringType })
        ) { backStackEntry ->
            val favId = backStackEntry.arguments?.getString("favId")
            TransferenciaEnviarScreen(
                aoVoltar = { navController.popBackStack() },
                favorecidoId = favId
            )
        }
        composable(Rotas.TransferenciaFavorecidos.rota) {
            FavorecidosScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.Deposito.rota) {
            DepositoScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.Pagamento.rota) {
            PagamentoScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.Cartoes.rota) {
            CartoesScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
        composable(Rotas.Investimento.rota) {
            InvestimentoScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
    }
}

