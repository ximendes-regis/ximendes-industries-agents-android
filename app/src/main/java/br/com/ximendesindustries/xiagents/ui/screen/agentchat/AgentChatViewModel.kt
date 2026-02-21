package br.com.ximendesindustries.xiagents.ui.screen.agentchat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.ximendesindustries.xiagents.core.util.Result
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
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val agentId: String = checkNotNull(savedStateHandle["agentId"])
    
    private val _uiState = MutableStateFlow<AgentChatUiState>(AgentChatUiState.Loading)
    val uiState: StateFlow<AgentChatUiState> = _uiState.asStateFlow()

    private val _messages = mutableListOf<ChatMessage>()

    init {
        // Inicializa o chat com uma mensagem vazia ou carrega histórico se necessário
        // Por enquanto, apenas configura o estado inicial com o nome do agente (seria ideal buscar o nome do agente também)
        _uiState.value = AgentChatUiState.Success(
            agentName = "Agente $agentId", // Idealmente buscaria o nome real
            messages = emptyList()
        )
        
        // Mensagem de boas vindas simulada localmente para UX imediata
        addMessage(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "Olá! Como posso ajudar você hoje?",
                isFromUser = false
            )
        )
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val currentState = _uiState.value
        if (currentState is AgentChatUiState.Success) {
            viewModelScope.launch {
                // 1. Adiciona mensagem do usuário na UI imediatamente
                val userMsg = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = content,
                    isFromUser = true
                )
                addMessage(userMsg)

                // 2. Chama o caso de uso para enviar a mensagem
                sendMessageUseCase(agentId, content).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            // Opcional: Mostrar indicador de "digitando..."
                        }
                        is Result.Success -> {
                            // Mapeia o AgentMessage (domínio) para ChatMessage (UI)
                            val agentResponse = result.data
                            val agentMsg = ChatMessage(
                                id = UUID.randomUUID().toString(),
                                content = agentResponse.message,
                                isFromUser = false
                            )
                            addMessage(agentMsg)
                        }
                        is Result.Error -> {
                            // Adiciona mensagem de erro como se fosse do sistema ou um balão de erro
                            val errorMsg = ChatMessage(
                                id = UUID.randomUUID().toString(),
                                content = "Erro ao enviar mensagem: ${result.message}",
                                isFromUser = false // Ou criar um tipo específico para erro
                            )
                            addMessage(errorMsg)
                        }
                    }
                }
            }
        }
    }

    private fun addMessage(message: ChatMessage) {
        _messages.add(message)
        val currentState = _uiState.value
        if (currentState is AgentChatUiState.Success) {
            _uiState.value = currentState.copy(messages = _messages.toList())
        }
    }
}
