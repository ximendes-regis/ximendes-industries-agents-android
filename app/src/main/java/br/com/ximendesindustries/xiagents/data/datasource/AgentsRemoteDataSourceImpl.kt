package br.com.ximendesindustries.xiagents.data.datasource

import br.com.ximendesindustries.xiagents.data.api.AgentsApi
import br.com.ximendesindustries.xiagents.data.model.request.ChatRequest
import br.com.ximendesindustries.xiagents.data.model.response.AgentResponse
import br.com.ximendesindustries.xiagents.data.model.response.ChatResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationDetailResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationListResponse
import javax.inject.Inject

class AgentsRemoteDataSourceImpl @Inject constructor(
    private val api: AgentsApi
) : AgentsRemoteDataSource {

    override suspend fun getAgents(): List<AgentResponse> = api.getAgents()

    override suspend fun getSessions(): ConversationListResponse = api.getPixelSessions()

    override suspend fun getSessionDetail(sessionId: String): ConversationDetailResponse =
        api.getPixelSessionById(sessionId)

    override suspend fun sendMessage(
        agentId: String,
        sessionId: String?,
        message: String,
    ): ChatResponse =
        api.sendMessage(
            agentId = agentId,
            request = ChatRequest(agentId = agentId, sessionId = sessionId ?: "", message = message)
        )
}
