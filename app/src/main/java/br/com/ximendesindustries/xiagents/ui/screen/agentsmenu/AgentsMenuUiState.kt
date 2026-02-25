package br.com.ximendesindustries.xiagents.ui.screen.agentsmenu

import br.com.ximendesindustries.xiagents.core.model.RequestUIState
import br.com.ximendesindustries.xiagents.domain.model.Agent

data class AgentsMenuUiState(
    val agents: List<Agent> = emptyList(),
    val requestUIState: RequestUIState = RequestUIState.Loading
)

