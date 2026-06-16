package com.example.financeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.domain.usecase.VerificarSessaoUseCase
import com.example.financeapp.ui.screens.configuracoes.ConfiguracoesScreen
import com.example.financeapp.ui.screens.home.HomeScreen
import com.example.financeapp.ui.screens.login.LoginScreen
import com.example.financeapp.ui.screens.perfil.PerfilScreen
import com.example.financeapp.ui.screens.privacidade.PrivacidadeScreen
import com.example.financeapp.ui.screens.registro.RegistroScreen
import com.example.financeapp.ui.screens.seguranca.SegurancaScreen
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
                aoAbrirConfiguracoes = {
                    navController.navigate(Rotas.Configuracoes.rota)
                }
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
    }
}

