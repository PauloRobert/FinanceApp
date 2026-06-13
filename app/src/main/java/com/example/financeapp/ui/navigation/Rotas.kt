package com.example.financeapp.ui.navigation

// Rotas de navegação do aplicativo
sealed class Rotas(val rota: String) {
    data object Home : Rotas("home")
    data object Configuracoes : Rotas("configuracoes")
}

