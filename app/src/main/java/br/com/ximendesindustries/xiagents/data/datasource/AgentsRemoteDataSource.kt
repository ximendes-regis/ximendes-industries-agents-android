package br.com.ximendesindustries.xiagents.data.datasource

import br.com.ximendesindustries.xiagents.data.api.AgentsApi
import br.com.ximendesindustries.xiagents.data.model.request.ChatRequest
import br.com.ximendesindustries.xiagents.data.model.response.AgentResponse
import br.com.ximendesindustries.xiagents.data.model.response.ChatResponse
import javax.inject.Inject

class AgentsRemoteDataSource @Inject constructor(
    private val api: AgentsApi
) {
    suspend fun getAgents(): List<AgentResponse> = api.getAgents()
    
    suspend fun sendMessage(agentId: String, message: String): ChatResponse = 
        api.sendMessage(ChatRequest(agentId = agentId, message = message))
}
