package com.example.financeapp.data.auth

import com.example.financeapp.data.remote.api.AuthInterceptor
import com.example.financeapp.data.remote.api.TransactionApi
import com.example.financeapp.data.remote.dto.LoginRequest
import com.example.financeapp.data.remote.dto.RegisterRequest
import com.example.financeapp.domain.model.ResultadoAuth
import com.example.financeapp.domain.model.Usuario
import com.example.financeapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

// Implementação do repositório de autenticação usando a API REST
class AuthRepositoryImpl(
    private val api: TransactionApi,
    private val tokenManager: TokenManager,
    private val authInterceptor: AuthInterceptor
) : AuthRepository {

    override suspend fun login(nomeUsuario: String, senha: String): ResultadoAuth {
        return try {
            val resposta = api.login(LoginRequest(nomeUsuario, senha))
            authInterceptor.atualizarToken(resposta.accessToken)

            // Decodificar informações básicas do token não é necessário aqui
            // Salvamos o username que o usuário informou
            tokenManager.salvarSessao(
                token = resposta.accessToken,
                idUsuario = nomeUsuario, // A API retorna o ID no token, mas usamos o username como referência
                nomeUsuario = nomeUsuario
            )

            ResultadoAuth.Sucesso(
                Usuario(
                    id = nomeUsuario,
                    nomeUsuario = nomeUsuario
                )
            )
        } catch (e: retrofit2.HttpException) {
            val mensagem = when (e.code()) {
                401 -> "Usuário ou senha inválidos"
                else -> "Erro ao fazer login: ${e.message()}"
            }
            ResultadoAuth.Erro(mensagem)
        } catch (e: Exception) {
            ResultadoAuth.Erro("Erro de conexão. Verifique sua internet.")
        }
    }

    override suspend fun registrar(nomeUsuario: String, senha: String): ResultadoAuth {
        return try {
            val resposta = api.registrar(RegisterRequest(nomeUsuario, senha))

            ResultadoAuth.Sucesso(
                Usuario(
                    id = resposta.id,
                    nomeUsuario = resposta.username,
                    ativo = resposta.isActive
                )
            )
        } catch (e: retrofit2.HttpException) {
            val mensagem = when (e.code()) {
                409 -> "Nome de usuário já está em uso"
                422 -> "Dados inválidos. Verifique os campos."
                else -> "Erro ao registrar: ${e.message()}"
            }
            ResultadoAuth.Erro(mensagem)
        } catch (e: Exception) {
            ResultadoAuth.Erro("Erro de conexão. Verifique sua internet.")
        }
    }

    override suspend fun logout() {
        tokenManager.limparSessao()
        authInterceptor.atualizarToken("")
    }

    override fun estaLogado(): Flow<Boolean> {
        return tokenManager.estaLogado()
    }

    override fun obterNomeUsuario(): Flow<String?> {
        return tokenManager.obterNomeUsuario()
    }
}
