package com.example.financeapp.ui.navigation

// Rotas de navegação do aplicativo
sealed class Rotas(val rota: String) {
    data object Login : Rotas("login")
    data object Registro : Rotas("registro")
    data object Home : Rotas("home")
    data object Configuracoes : Rotas("configuracoes")
    data object Perfil : Rotas("perfil")
    data object Seguranca : Rotas("seguranca")
    data object Privacidade : Rotas("privacidade")
}

