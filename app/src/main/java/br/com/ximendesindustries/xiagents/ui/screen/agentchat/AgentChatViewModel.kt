package br.com.ximendesindustries.xiagents.ui.screen.agentchat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.core.util.isPixelAgent
import br.com.ximendesindustries.xiagents.domain.model.ChatSession
import br.com.ximendesindustries.xiagents.domain.model.SessionDetail
import br.com.ximendesindustries.xiagents.domain.usecase.GetSessionDetailUseCase
import br.com.ximendesindustries.xiagents.domain.usecase.GetSessionsUseCase
import br.com.ximendesindustries.xiagents.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _uiState = MutableStateFlow<AgentChatUiState>(AgentChatUiState.Loading)
    val uiState: StateFlow<AgentChatUiState> = _uiState.asStateFlow()

    /** Sessões da API + novas criadas localmente nesta tela (nova conversa + envio). */
    private val _sessions = mutableListOf<ChatSession>()

    /** Mensagens por sessão: sessionId -> mensagens. "nova conversa" usa lista em _messages. */
    private val _messagesBySession = mutableMapOf<String, MutableList<ChatMessage>>()

    /** Mensagens da conversa atual (nova conversa ou sessão selecionada). */
    private val _messages = mutableListOf<ChatMessage>()

    /** ID da sessão selecionada; null = nova conversa. */
    private var selectedSessionId: String? = null

    init {
        _uiState.value = AgentChatUiState.Success(
            agentName = agentId,
            messages = emptyList(),
            sessions = _sessions.toList(),
            selectedSession = null
        )
        addWelcomeMessage()

        if (agentId.isPixelAgent()) loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            getSessionsUseCase(agentId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val apiIds = result.data.map { it.id }.toSet()
                        val localOnly = _sessions.filter { it.id !in apiIds }
                        _sessions.clear()
                        _sessions.addAll(result.data)
                        _sessions.addAll(0, localOnly)
                        emitSuccess()
                    }

                    is Result.Loading, is Result.Error -> { /* opcional: tratar erro/loading */
                    }
                }
            }
        }
    }

    private fun addWelcomeMessage() {
        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "Olá! Como posso ajudar você hoje?",
                isFromUser = false
            )
        )
    }

    /** Seleciona uma sessão (null = nova conversa). Se for sessão da API, carrega a conversa. */
    fun selectSession(session: ChatSession?) {
        selectedSessionId?.let { id ->
            _messagesBySession.getOrPut(id) { mutableListOf() }.clear()
            _messagesBySession[id]!!.addAll(_messages)
        }
        _messages.clear()
        selectedSessionId = session?.id
        if (session != null) {
            val cached = _messagesBySession[session.id].orEmpty()
            if (cached.isNotEmpty()) {
                _messages.addAll(cached)
                emitSuccess()
            } else {
                loadSessionDetail(session.id)
            }
        } else {
            addWelcomeMessage()
            emitSuccess()
        }
    }

    private fun loadSessionDetail(sessionId: String) {
        setLoadingSessionDetail(true)
        viewModelScope.launch {
            getSessionDetailUseCase(sessionId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val messages = sessionDetailToMessages(result.data)
                        _messages.clear()
                        _messages.addAll(messages)
                        _messagesBySession[sessionId] = _messages.toMutableList()
                        if (_messages.isEmpty()) addWelcomeMessage()
                        setLoadingSessionDetail(false)
                        emitSuccess()
                    }

                    is Result.Error -> {
                        addWelcomeMessage()
                        setLoadingSessionDetail(false)
                        emitSuccess()
                    }

                    is Result.Loading -> { /* mantém loading */
                    }
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

    private fun setLoadingSessionDetail(loading: Boolean) {
        val current = _uiState.value
        if (current is AgentChatUiState.Success) {
            _uiState.value = current.copy(isLoadingSessionDetail = loading)
        }
    }

    private fun emitSuccess() {
        val current = _uiState.value
        if (current is AgentChatUiState.Success) {
            _uiState.value = current.copy(
                messages = _messages.toList(),
                sessions = _sessions.toList(),
                selectedSession = _sessions.find { it.id == selectedSessionId },
                isLoadingSessionDetail = current.isLoadingSessionDetail
            )
        }
    }

    /**
     * Aplica o sessionId retornado pela API como nova sessão selecionada (fluxo: enviou sem session → backend devolve session_id).
     */
    private fun selectSessionFromResponse(sessionId: String, firstMessageTitle: String) {
        if (_sessions.any { it.id == sessionId }) {
            selectedSessionId = sessionId
            _messagesBySession[sessionId] = _messages.toMutableList()
        } else {
            val title = firstMessageTitle.take(30).let { if (it.length == 30) "$it..." else it }
                .ifBlank { "Nova conversa" }
            val newSession = ChatSession(
                id = sessionId,
                agentId = agentId,
                title = title
            )
            _sessions.add(0, newSession)
            selectedSessionId = sessionId
            _messagesBySession[sessionId] = _messages.toMutableList()
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val currentState = _uiState.value
        if (currentState is AgentChatUiState.Success) {
            viewModelScope.launch {
                val userMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = content,
                    isFromUser = true
                )
                addMessage(userMsg)

                val sessionIdToSend = selectedSessionId
                sendMessageUseCase(agentId, sessionIdToSend, content).collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val agentResponse = result.data
                            val agentMsg = ChatMessage(
                                id = UUID.randomUUID().toString(),
                                content = agentResponse.message,
                                isFromUser = false
                            )
                            addMessage(agentMsg)
                            if (sessionIdToSend == null && agentResponse.sessionId != null) {
                                val title =
                                    content.take(30).let { if (it.length == 30) "$it..." else it }
                                selectSessionFromResponse(
                                    agentResponse.sessionId,
                                    title.ifBlank { "Nova conversa" })
                            }
                            emitSuccess()
                        }

                        is Result.Error -> {
                            val errorMsg = ChatMessage(
                                id = UUID.randomUUID().toString(),
                                content = "Erro ao enviar mensagem: ${result.message}",
                                isFromUser = false
                            )
                            addMessage(errorMsg)
                            emitSuccess()
                        }

                        is Result.Loading -> {}
                    }
                }
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _messages.add(message)
        emitSuccess()
    }
}
