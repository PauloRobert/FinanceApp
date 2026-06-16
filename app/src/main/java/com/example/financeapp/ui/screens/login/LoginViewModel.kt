package com.example.financeapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.model.ResultadoAuth
import com.example.financeapp.domain.usecase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun atualizarNomeUsuario(valor: String) {
        _estado.value = _estado.value.copy(nomeUsuario = valor, mensagemErro = null)
    }

    fun atualizarSenha(valor: String) {
        _estado.value = _estado.value.copy(senha = valor, mensagemErro = null)
    }

    fun login() {
        val estadoAtual = _estado.value

        // Validação de campos
        if (estadoAtual.nomeUsuario.isBlank()) {
            _estado.value = estadoAtual.copy(mensagemErro = "Informe o nome de usuário")
            return
        }
        if (estadoAtual.senha.isBlank()) {
            _estado.value = estadoAtual.copy(mensagemErro = "Informe a senha")
            return
        }

        viewModelScope.launch {
            _estado.value = estadoAtual.copy(carregando = true, mensagemErro = null)

            val resultado = loginUseCase(estadoAtual.nomeUsuario, estadoAtual.senha)

            when (resultado) {
                is ResultadoAuth.Sucesso -> {
                    _estado.value = _estado.value.copy(
                        carregando = false,
                        loginRealizado = true
                    )
                }
                is ResultadoAuth.Erro -> {
                    _estado.value = _estado.value.copy(
                        carregando = false,
                        mensagemErro = resultado.mensagem
                    )
                }
                is ResultadoAuth.Carregando -> {
                    // Estado intermediário, já tratado acima
                }
            }
        }
    }

    fun limparErro() {
        _estado.value = _estado.value.copy(mensagemErro = null)
    }
}
