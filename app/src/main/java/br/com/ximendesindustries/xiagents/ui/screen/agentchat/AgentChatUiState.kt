package br.com.ximendesindustries.xiagents.ui.screen.agentchat

import androidx.compose.runtime.Immutable
import br.com.ximendesindustries.xiagents.domain.model.ChatSession

@Immutable
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface AgentChatUiState {
    data object Loading : AgentChatUiState
    data class Success(
        val agentName: String,
        val messages: List<ChatMessage>,
        val sessions: List<ChatSession> = emptyList(),
        val selectedSession: ChatSession? = null,
        val isLoadingSessionDetail: Boolean = false
    ) : AgentChatUiState
    data class Error(val message: String) : AgentChatUiState
}
