package com.example.financeapp.ui.screens.seguranca

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.remote.dto.PasswordChangeRequest
import com.example.financeapp.data.remote.dto.SecurityUpdateRequest
import com.example.financeapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SegurancaViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(SegurancaUiState())
    val estado: StateFlow<SegurancaUiState> = _estado.asStateFlow()

    init {
        carregarSeguranca()
    }

    private fun carregarSeguranca() {
        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                val seguranca = userRepository.obterSeguranca()
                _estado.update {
                    seguranca.toUiState().copy(carregando = false)
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(carregando = false, mensagemErro = "Erro ao carregar configurações")
                }
            }
        }
    }

    fun alternarTwoFactor(valor: Boolean) {
        atualizarConfiguracoes(SecurityUpdateRequest(twoFactorEnabled = valor))
        _estado.update { it.copy(twoFactorEnabled = valor) }
    }

    fun alternarBiometria(valor: Boolean) {
        atualizarConfiguracoes(SecurityUpdateRequest(biometricEnabled = valor))
        _estado.update { it.copy(biometricEnabled = valor) }
    }

    fun alternarNotificacaoLogin(valor: Boolean) {
        atualizarConfiguracoes(SecurityUpdateRequest(loginNotificationEnabled = valor))
        _estado.update { it.copy(loginNotificationEnabled = valor) }
    }

    private fun atualizarConfiguracoes(dados: SecurityUpdateRequest) {
        viewModelScope.launch {
            try {
                userRepository.atualizarSeguranca(dados)
            } catch (e: Exception) {
                _estado.update { it.copy(mensagemErro = "Erro ao atualizar") }
            }
        }
    }

    fun mostrarTrocaSenha() {
        _estado.update {
            it.copy(
                mostrarTrocaSenha = true,
                senhaAtual = "",
                novaSenha = "",
                confirmarNovaSenha = ""
            )
        }
    }

    fun ocultarTrocaSenha() {
        _estado.update { it.copy(mostrarTrocaSenha = false) }
    }

    fun atualizarSenhaAtual(valor: String) {
        _estado.update { it.copy(senhaAtual = valor) }
    }

    fun atualizarNovaSenha(valor: String) {
        _estado.update { it.copy(novaSenha = valor) }
    }

    fun atualizarConfirmarNovaSenha(valor: String) {
        _estado.update { it.copy(confirmarNovaSenha = valor) }
    }

    fun alterarSenha() {
        val state = _estado.value

        if (state.senhaAtual.length < 6) {
            _estado.update { it.copy(mensagemErro = "Senha atual inválida") }
            return
        }
        if (state.novaSenha.length < 6) {
            _estado.update { it.copy(mensagemErro = "Nova senha deve ter no mínimo 6 caracteres") }
            return
        }
        if (state.novaSenha != state.confirmarNovaSenha) {
            _estado.update { it.copy(mensagemErro = "As senhas não coincidem") }
            return
        }

        viewModelScope.launch {
            _estado.update { it.copy(trocandoSenha = true) }
            try {
                val mensagem = userRepository.alterarSenha(
                    PasswordChangeRequest(
                        currentPassword = state.senhaAtual,
                        newPassword = state.novaSenha
                    )
                )
                _estado.update {
                    it.copy(
                        trocandoSenha = false,
                        mostrarTrocaSenha = false,
                        mensagemSucesso = mensagem,
                        senhaAtual = "",
                        novaSenha = "",
                        confirmarNovaSenha = ""
                    )
                }
                carregarSeguranca()
            } catch (e: retrofit2.HttpException) {
                val msg = when (e.code()) {
                    400 -> "Senha atual incorreta"
                    else -> "Erro ao alterar senha"
                }
                _estado.update { it.copy(trocandoSenha = false, mensagemErro = msg) }
            } catch (e: Exception) {
                _estado.update { it.copy(trocandoSenha = false, mensagemErro = "Erro de conexão") }
            }
        }
    }

    fun limparMensagens() {
        _estado.update { it.copy(mensagemSucesso = null, mensagemErro = null) }
    }
}
