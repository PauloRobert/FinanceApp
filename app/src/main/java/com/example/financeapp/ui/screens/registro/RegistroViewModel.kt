package com.example.financeapp.ui.screens.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.model.ResultadoAuth
import com.example.financeapp.domain.usecase.RegistrarUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val registrarUseCase: RegistrarUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun atualizarNomeUsuario(valor: String) {
        _estado.value = _estado.value.copy(nomeUsuario = valor, mensagemErro = null)
    }

    fun atualizarSenha(valor: String) {
        _estado.value = _estado.value.copy(senha = valor, mensagemErro = null)
    }

    fun atualizarConfirmacaoSenha(valor: String) {
        _estado.value = _estado.value.copy(confirmacaoSenha = valor, mensagemErro = null)
    }

    fun registrar() {
        val estadoAtual = _estado.value

        // Validações
        if (estadoAtual.nomeUsuario.length < 3) {
            _estado.value = estadoAtual.copy(mensagemErro = "Usuário deve ter pelo menos 3 caracteres")
            return
        }
        if (estadoAtual.senha.length < 6) {
            _estado.value = estadoAtual.copy(mensagemErro = "Senha deve ter pelo menos 6 caracteres")
            return
        }
        if (estadoAtual.senha != estadoAtual.confirmacaoSenha) {
            _estado.value = estadoAtual.copy(mensagemErro = "As senhas não coincidem")
            return
        }

        viewModelScope.launch {
            _estado.value = estadoAtual.copy(carregando = true, mensagemErro = null)

            val resultado = registrarUseCase(estadoAtual.nomeUsuario, estadoAtual.senha)

            when (resultado) {
                is ResultadoAuth.Sucesso -> {
                    _estado.value = _estado.value.copy(
                        carregando = false,
                        registroRealizado = true
                    )
                }
                is ResultadoAuth.Erro -> {
                    _estado.value = _estado.value.copy(
                        carregando = false,
                        mensagemErro = resultado.mensagem
                    )
                }
                is ResultadoAuth.Carregando -> {}
            }
        }
    }

    fun limparErro() {
        _estado.value = _estado.value.copy(mensagemErro = null)
    }
}
