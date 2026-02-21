package br.com.ximendesindustries.xiagents.ui.screen.agentsmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.usecase.GetAgentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentsMenuViewModel @Inject constructor(
    private val getAgentsUseCase: GetAgentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AgentsMenuUiState>(AgentsMenuUiState.Loading)
    val uiState: StateFlow<AgentsMenuUiState> = _uiState.asStateFlow()

    init {
        loadAgents()
    }

    private fun loadAgents() {
        viewModelScope.launch {
            getAgentsUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.value = AgentsMenuUiState.Loading
                    }
                    is Result.Success -> {
                        val agents = result.data.map { domainAgent ->
                            Agent(
                                id = domainAgent.id,
                                name = domainAgent.name,
                                description = domainAgent.description,
                                iconRes = null // TODO: Implementar carregamento de imagem via URL se necessário
                            )
                        }
                        _uiState.value = AgentsMenuUiState.Success(agents)
                    }
                    is Result.Error -> {
                        _uiState.value = AgentsMenuUiState.Error(result.message ?: "Erro desconhecido")
                    }
                }
            }
        }
    }
}
