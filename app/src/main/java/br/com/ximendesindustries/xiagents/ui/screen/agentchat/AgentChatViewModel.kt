package br.com.ximendesindustries.xiagents.ui.screen.agentchat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ximendesindustries.xiagents.core.model.RequestUIState
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.core.util.isPixelAgent
import br.com.ximendesindustries.xiagents.domain.model.ChatSession
import br.com.ximendesindustries.xiagents.domain.model.SessionDetail
import br.com.ximendesindustries.xiagents.domain.usecase.GetSessionDetailUseCase
import br.com.ximendesindustries.xiagents.domain.usecase.GetSessionsUseCase
import br.com.ximendesindustries.xiagents.domain.usecase.SendMessageUseCase
import br.com.ximendesindustries.xiagents.ui.screen.agentchat.model.AgentChatViewModelAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AgentChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSessionsUseCase: GetSessionsUseCase,
    private val getSessionDetailUseCase: GetSessionDetailUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val agentId: String = checkNotNull(savedStateHandle["agentId"])

    private val _uiState = MutableStateFlow(AgentChatUiState())
    val uiState: StateFlow<AgentChatUiState> = _uiState.asStateFlow()

    fun performAction(action: AgentChatViewModelAction) {
        when (action) {
            AgentChatViewModelAction.StartAction -> start()
            is AgentChatViewModelAction.SendMessageAction -> sendMessage(action.content)
            is AgentChatViewModelAction.SelectSessionAction -> selectSession(action.session)
        }
    }

    private fun start() {
        val welcome = createWelcomeMessage()
        _uiState.update {
            it.copy(
                requestUIState = RequestUIState.Success,
                agentName = agentId,
                messages = listOf(welcome),
                sessions = emptyList(),
                selectedSession = null,
                messagesBySession = emptyMap()
            )
        }
        if (agentId.isPixelAgent()) loadSessions()
    }

    private fun createWelcomeMessage() = ChatMessage(
        id = UUID.randomUUID().toString(),
        content = "Olá Sr. Ximendes",
        isFromUser = false
    )

    private fun loadSessions() {
        viewModelScope.launch {
            getSessionsUseCase(agentId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val current = _uiState.value
                        val apiIds = result.data.map { it.id }.toSet()
                        val localOnly = current.sessions.filter { it.id !in apiIds }
                        val newSessions = result.data + localOnly
                        _uiState.update { it.copy(sessions = newSessions) }
                    }
                    is Result.Loading, is Result.Error -> { }
                }
            }
        }
    }

    private fun selectSession(session: ChatSession?) {
        val current = _uiState.value
        val newMap = if (current.selectedSession != null) {
            current.messagesBySession + (current.selectedSession.id to current.messages)
        } else {
            current.messagesBySession
        }

        if (session == null) {
            _uiState.update {
                it.copy(
                    messagesBySession = newMap,
                    selectedSession = null,
                    messages = listOf(createWelcomeMessage())
                )
            }
            return
        }

        val cached = newMap[session.id].orEmpty()
        if (cached.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    messagesBySession = newMap,
                    selectedSession = session,
                    messages = cached
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    messagesBySession = newMap,
                    selectedSession = session,
                    messages = emptyList(),
                    isLoadingSessionDetail = true
                )
            }
            loadSessionDetail(session.id)
        }
    }

    private fun loadSessionDetail(sessionId: String) {
        viewModelScope.launch {
            getSessionDetailUseCase(sessionId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        var messages = sessionDetailToMessages(result.data)
                        if (messages.isEmpty()) messages = listOf(createWelcomeMessage())
                        _uiState.update {
                            it.copy(
                                messages = messages,
                                messagesBySession = it.messagesBySession + (sessionId to messages),
                                isLoadingSessionDetail = false
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                messages = listOf(createWelcomeMessage()),
                                isLoadingSessionDetail = false
                            )
                        }
                    }
                    is Result.Loading -> { }
                }
            }
        }
    }

    private fun sessionDetailToMessages(detail: SessionDetail): List<ChatMessage> =
        detail.turns.flatMapIndexed { index, turn ->
            listOf(
                ChatMessage(
                    id = "${detail.sessionId}_${index}_user",
                    content = turn.userMessage,
                    isFromUser = true,
                    timestamp = turn.createdAt
                ),
                ChatMessage(
                    id = "${detail.sessionId}_${index}_assistant",
                    content = turn.assistantMessage,
                    isFromUser = false,
                    timestamp = turn.createdAt
                )
            )
        }

    private fun sendMessage(content: String) {
        if (content.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = content,
            isFromUser = true
        )
        _uiState.update { it.copy(messages = it.messages + userMsg) }

        val sessionIdToSend = _uiState.value.selectedSession?.id

        viewModelScope.launch {
            sendMessageUseCase(agentId, sessionIdToSend, content).collect { result ->
                val current = _uiState.value
                when (result) {
                    is Result.Success -> {
                        val agentMsg = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            content = result.data.message,
                            isFromUser = false
                        )
                        val newMessages = current.messages + agentMsg

                        if (sessionIdToSend == null && result.data.sessionId != null) {
                            val sessionId = result.data.sessionId
                            val title = content.take(30).let { if (it.length == 30) "$it..." else it }
                                .ifBlank { "Nova conversa" }
                            val newSession = ChatSession(
                                id = sessionId,
                                agentId = agentId,
                                title = title
                            )
                            _uiState.update {
                                it.copy(
                                    messages = newMessages,
                                    sessions = listOf(newSession) + it.sessions,
                                    selectedSession = newSession,
                                    messagesBySession = it.messagesBySession + (sessionId to newMessages)
                                )
                            }
                        } else {
                            val sid = current.selectedSession?.id
                            val newMap = if (sid != null) {
                                current.messagesBySession + (sid to newMessages)
                            } else {
                                current.messagesBySession
                            }
                            _uiState.update {
                                it.copy(messages = newMessages, messagesBySession = newMap)
                            }
                        }
                    }
                    is Result.Error -> {
                        val errorMsg = ChatMessage(
                            id = UUID.randomUUID().toString(),
                            content = "Erro ao enviar mensagem: ${result.message}",
                            isFromUser = false
                        )
                        _uiState.update { it.copy(messages = current.messages + errorMsg) }
                    }
                    is Result.Loading -> { }
                }
            }
        }
    }
}
