package com.example.financeapp.ui.screens.configuracoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.provider.RepositoryProvider
import com.example.financeapp.domain.model.OrigemDados
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfiguracoesViewModel(
    private val repositoryProvider: RepositoryProvider
) : ViewModel() {

    private val _origemSelecionada = MutableStateFlow(OrigemDados.FIREBASE)
    val origemSelecionada: StateFlow<OrigemDados> = _origemSelecionada.asStateFlow()

    init {
        viewModelScope.launch {
            repositoryProvider.origemAtual.collect { origem ->
                _origemSelecionada.value = origem
            }
        }
    }

    fun selecionarOrigem(novaOrigem: OrigemDados) {
        viewModelScope.launch {
            repositoryProvider.trocarOrigem(novaOrigem)
        }
    }
}

