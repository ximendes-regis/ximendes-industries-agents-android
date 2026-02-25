package br.com.ximendesindustries.xiagents.ui.screen.agentchat.model

import br.com.ximendesindustries.xiagents.domain.model.ChatSession

sealed class AgentChatViewModelAction {

    data object StartAction : AgentChatViewModelAction()
    data class SendMessageAction(val content: String) : AgentChatViewModelAction()
    data class SelectSessionAction(val session: ChatSession?) : AgentChatViewModelAction()
}
