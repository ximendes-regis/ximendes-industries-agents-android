package br.com.ximendesindustries.xiagents.ui.screen.agentsmenu

import androidx.compose.runtime.Immutable

@Immutable
data class Agent(
    val id: String,
    val name: String,
    val description: String,
    val iconRes: Int? = null // Opcional, se tiver ícones drawable
)

sealed interface AgentsMenuUiState {
    data object Loading : AgentsMenuUiState
    data class Success(val agents: List<Agent>) : AgentsMenuUiState
    data class Error(val message: String) : AgentsMenuUiState
}
