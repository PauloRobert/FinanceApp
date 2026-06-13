package com.example.financeapp.data.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.financeapp.domain.model.OrigemDados
import com.example.financeapp.domain.repository.DataSourceConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensão para criar o DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "configuracoes"
)

class DataSourceConfigRepositoryImpl(
    private val contexto: Context
) : DataSourceConfigRepository {

    companion object {
        private val CHAVE_ORIGEM = stringPreferencesKey("origem_dados")
    }

    override fun obterOrigemAtual(): Flow<OrigemDados> {
        return contexto.dataStore.data.map { preferencias ->
            val valor = preferencias[CHAVE_ORIGEM] ?: OrigemDados.FIREBASE.name
            OrigemDados.valueOf(valor)
        }
    }

    override suspend fun salvarOrigem(origem: OrigemDados) {
        contexto.dataStore.edit { preferencias ->
            preferencias[CHAVE_ORIGEM] = origem.name
        }
    }
}

