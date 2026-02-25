package br.com.ximendesindustries.xiagents.data.datasource

import br.com.ximendesindustries.xiagents.data.api.AgentsApi
import br.com.ximendesindustries.xiagents.data.model.request.ChatRequest
import br.com.ximendesindustries.xiagents.data.model.response.AgentResponse
import br.com.ximendesindustries.xiagents.data.model.response.ChatResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationDetailResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationListResponse
import javax.inject.Inject

class AgentsRemoteDataSource @Inject constructor(
    private val api: AgentsApi
) {
    suspend fun getAgents(): List<AgentResponse> = api.getAgents()

    suspend fun getSessions(): ConversationListResponse = api.getPixelSessions()

    suspend fun getSessionDetail(sessionId: String): ConversationDetailResponse =
        api.getPixelSessionById(sessionId)

    suspend fun sendMessage(
        agentId: String,
        sessionId: String?,
        message: String,
    ): ChatResponse =
        api.sendMessage(
            agentId = agentId,
            request = ChatRequest(agentId = agentId, sessionId = sessionId ?: "", message = message)
        )
}
