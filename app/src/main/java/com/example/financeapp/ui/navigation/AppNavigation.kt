package com.example.financeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.configuracoes.ConfiguracoesScreen
import com.example.financeapp.ui.screens.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rotas.Home.rota
    ) {
        composable(Rotas.Home.rota) {
            HomeScreen(
                aoAbrirConfiguracoes = {
                    navController.navigate(Rotas.Configuracoes.rota)
                }
            )
        }
        composable(Rotas.Configuracoes.rota) {
            ConfiguracoesScreen(
                aoVoltar = { navController.popBackStack() }
            )
        }
    }
}

