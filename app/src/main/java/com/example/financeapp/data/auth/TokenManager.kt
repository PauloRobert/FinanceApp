package com.example.financeapp.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// DataStore dedicado para dados de autenticação
private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_preferencias"
)

// Gerencia o token JWT e dados da sessão no DataStore
class TokenManager(private val contexto: Context) {

    companion object {
        private val CHAVE_TOKEN = stringPreferencesKey("jwt_token")
        private val CHAVE_NOME_USUARIO = stringPreferencesKey("nome_usuario")
        private val CHAVE_ID_USUARIO = stringPreferencesKey("id_usuario")
    }

    // Salva o token e dados do usuário após login
    suspend fun salvarSessao(token: String, idUsuario: String, nomeUsuario: String) {
        contexto.authDataStore.edit { preferencias ->
            preferencias[CHAVE_TOKEN] = token
            preferencias[CHAVE_ID_USUARIO] = idUsuario
            preferencias[CHAVE_NOME_USUARIO] = nomeUsuario
        }
    }

    // Limpa todos os dados da sessão (logout)
    suspend fun limparSessao() {
        contexto.authDataStore.edit { preferencias ->
            preferencias.clear()
        }
    }

    // Observa se há token válido salvo
    fun obterToken(): Flow<String?> {
        return contexto.authDataStore.data.map { preferencias ->
            preferencias[CHAVE_TOKEN]
        }
    }

    // Observa o nome do usuário logado
    fun obterNomeUsuario(): Flow<String?> {
        return contexto.authDataStore.data.map { preferencias ->
            preferencias[CHAVE_NOME_USUARIO]
        }
    }

    // Verifica se há sessão ativa
    fun estaLogado(): Flow<Boolean> {
        return contexto.authDataStore.data.map { preferencias ->
            preferencias[CHAVE_TOKEN] != null
        }
    }
}
