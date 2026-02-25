package br.com.ximendesindustries.xiagents.ui.screen.agentsmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ximendesindustries.xiagents.core.model.RequestUIState
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.usecase.GetAgentsUseCase
import br.com.ximendesindustries.xiagents.ui.screen.agentsmenu.model.AgentsMenuViewModelAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentsMenuViewModel @Inject constructor(
    private val getAgentsUseCase: GetAgentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgentsMenuUiState())
    val uiState: StateFlow<AgentsMenuUiState> = _uiState.asStateFlow()

    fun performAction(action: AgentsMenuViewModelAction) {
        when (action) {
            AgentsMenuViewModelAction.StartAction -> start()
        }
    }

    private fun start() {
        loadAgents()
    }

    private fun loadAgents() {
        viewModelScope.launch {
            getAgentsUseCase().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        updateRequestUIState(RequestUIState.Loading)
                    }
                    is Result.Success -> {
                        val agents = result.data.map { domainAgent ->
                            Agent(
                                id = domainAgent.id,
                                name = domainAgent.name,
                                description = domainAgent.description,
                                iconUrl = null
                            )
                        }
                        updateRequestUIState(RequestUIState.Success)
                        handlesSuccess(agents = agents)
                    }
                    is Result.Error -> {
                        updateRequestUIState(RequestUIState.Error)
                    }
                }
            }
        }
    }

    private fun updateRequestUIState(requestUIState: RequestUIState) {
        _uiState.update { it.copy(requestUIState = requestUIState) }
    }

    private fun handlesSuccess(agents: List<Agent>) {
        _uiState.update { it.copy(agents = agents) }
    }
}
