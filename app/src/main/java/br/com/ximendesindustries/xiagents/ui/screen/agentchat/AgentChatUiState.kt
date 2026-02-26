package br.com.ximendesindustries.xiagents.ui.screen.agentchat

import androidx.compose.runtime.Immutable
import br.com.ximendesindustries.xiagents.core.model.RequestUIState
import br.com.ximendesindustries.xiagents.domain.model.ChatSession

@Immutable
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AgentChatUiState(
    val requestUIState: RequestUIState = RequestUIState.Loading,
    val errorMessage: String = "",
    val agentName: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val sessions: List<ChatSession> = emptyList(),
    val selectedSession: ChatSession? = null,
    val messagesBySession: Map<String, List<ChatMessage>> = emptyMap(),
    val isLoadingSessionDetail: Boolean = false,
    val isSendingMessage: Boolean = false
)
