package com.example.financeapp.ui.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.ProfileUpdateRequest
import com.example.financeapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(PerfilUiState())
    val estado: StateFlow<PerfilUiState> = _estado.asStateFlow()

    init {
        carregarPerfil()
    }

    private fun carregarPerfil() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val perfil = userRepository.obterPerfil()
                _estado.update {
                    perfil.toUiState().copy(carregando = false)
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        carregando = false,
                        mensagemErro = "Erro ao carregar perfil"
                    )
                }
            }
        }
    }

    fun atualizarNomeExibicao(valor: String) {
        _estado.update { it.copy(displayName = valor) }
    }

    fun atualizarEmail(valor: String) {
        _estado.update { it.copy(email = valor) }
    }

    fun atualizarTelefone(valor: String) {
        _estado.update { it.copy(phone = valor) }
    }

    fun salvarPerfil() {
        viewModelScope.launch {
            _estado.update { it.copy(salvando = true) }
            try {
                val dados = ProfileUpdateRequest(
                    displayName = _estado.value.displayName.ifBlank { null },
                    email = _estado.value.email.ifBlank { null },
                    phone = _estado.value.phone.ifBlank { null }
                )
                val perfil = userRepository.atualizarPerfil(dados)
                _estado.update {
                    perfil.toUiState().copy(
                        salvando = false,
                        mensagemSucesso = "Perfil atualizado com sucesso"
                    )
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(
                        salvando = false,
                        mensagemErro = "Erro ao salvar perfil"
                    )
                }
            }
        }
    }

    fun limparMensagens() {
        _estado.update { it.copy(mensagemSucesso = null, mensagemErro = null) }
    }
}
