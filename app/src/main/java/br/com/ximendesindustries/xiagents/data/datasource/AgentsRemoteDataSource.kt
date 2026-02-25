package br.com.ximendesindustries.xiagents.data.datasource

import br.com.ximendesindustries.xiagents.data.model.response.AgentResponse
import br.com.ximendesindustries.xiagents.data.model.response.ChatResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationDetailResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationListResponse

interface AgentsRemoteDataSource {

    suspend fun getAgents(): List<AgentResponse>

    suspend fun getSessions(): ConversationListResponse

    suspend fun getSessionDetail(sessionId: String): ConversationDetailResponse

    suspend fun sendMessage(
        agentId: String,
        sessionId: String?,
        message: String,
    ): ChatResponse
}
