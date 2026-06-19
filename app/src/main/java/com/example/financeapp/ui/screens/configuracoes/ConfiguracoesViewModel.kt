package com.example.financeapp.ui.screens.configuracoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.usecase.LogoutUseCase
import kotlinx.coroutines.launch

class ConfiguracoesViewModel(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    fun logout(aoFinalizar: () -> Unit) {
        viewModelScope.launch {
            logoutUseCase()
            aoFinalizar()
        }
    }
}

