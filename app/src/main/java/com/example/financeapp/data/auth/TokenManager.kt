package com.example.financeapp.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_preferencias"
)

class TokenManager(private val contexto: Context) {

    companion object {
        private val CHAVE_TOKEN = stringPreferencesKey("jwt_token")
        private val CHAVE_NOME_USUARIO = stringPreferencesKey("nome_usuario")
        private val CHAVE_ID_USUARIO = stringPreferencesKey("id_usuario")
        private val CHAVE_LOGIN_TIMESTAMP = longPreferencesKey("login_timestamp")
        private const val SESSION_TIMEOUT_MS = 24 * 60 * 60 * 1000L // 24 horas
    }

    suspend fun salvarSessao(token: String, idUsuario: String, nomeUsuario: String) {
        contexto.authDataStore.edit { preferencias ->
            preferencias[CHAVE_TOKEN] = token
            preferencias[CHAVE_ID_USUARIO] = idUsuario
            preferencias[CHAVE_NOME_USUARIO] = nomeUsuario
            preferencias[CHAVE_LOGIN_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    suspend fun limparSessao() {
        contexto.authDataStore.edit { preferencias ->
            preferencias.clear()
        }
    }

    fun obterToken(): Flow<String?> {
        return contexto.authDataStore.data.map { preferencias ->
            preferencias[CHAVE_TOKEN]
        }
    }

    fun obterNomeUsuario(): Flow<String?> {
        return contexto.authDataStore.data.map { preferencias ->
            preferencias[CHAVE_NOME_USUARIO]
        }
    }

    fun estaLogado(): Flow<Boolean> {
        return contexto.authDataStore.data.map { preferencias ->
            val token = preferencias[CHAVE_TOKEN]
            val timestamp = preferencias[CHAVE_LOGIN_TIMESTAMP] ?: 0L
            val agora = System.currentTimeMillis()
            val sessaoValida = (agora - timestamp) < SESSION_TIMEOUT_MS
            token != null && sessaoValida
        }
    }

    suspend fun verificarELimparSessaoExpirada() {
        contexto.authDataStore.edit { preferencias ->
            val timestamp = preferencias[CHAVE_LOGIN_TIMESTAMP] ?: 0L
            val agora = System.currentTimeMillis()
            if ((agora - timestamp) >= SESSION_TIMEOUT_MS && preferencias[CHAVE_TOKEN] != null) {
                preferencias.clear()
            }
        }
    }
}
