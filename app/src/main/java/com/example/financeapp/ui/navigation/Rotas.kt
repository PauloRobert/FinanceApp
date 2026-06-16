package com.example.financeapp.ui.navigation

// Rotas de navegação do aplicativo
sealed class Rotas(val rota: String) {
    data object Splash : Rotas("splash")
    data object Login : Rotas("login")
    data object Registro : Rotas("registro")
    data object Home : Rotas("home")
    data object Configuracoes : Rotas("configuracoes")
    data object Perfil : Rotas("perfil")
    data object Seguranca : Rotas("seguranca")
    data object Privacidade : Rotas("privacidade")
    data object PixHub : Rotas("pix_hub")
    data object PixEnviar : Rotas("pix_enviar")
    data object PixEnviarFavorito : Rotas("pix_enviar/{chave}") {
        fun criarRota(chave: String) = "pix_enviar/$chave"
    }
    data object PixReceber : Rotas("pix_receber")
    data object PixChaves : Rotas("pix_chaves")
    data object PixHistorico : Rotas("pix_historico")
    data object TransferenciaHub : Rotas("transferencia_hub")
    data object TransferenciaEnviar : Rotas("transferencia_enviar")
    data object TransferenciaEnviarFavorecido : Rotas("transferencia_enviar/{favId}") {
        fun criarRota(favId: String) = "transferencia_enviar/$favId"
    }
    data object TransferenciaFavorecidos : Rotas("transferencia_favorecidos")
    data object Deposito : Rotas("deposito")
    data object Pagamento : Rotas("pagamento")
    data object Cartoes : Rotas("cartoes")
    data object Investimento : Rotas("investimento")
    data object Extrato : Rotas("extrato")
}

