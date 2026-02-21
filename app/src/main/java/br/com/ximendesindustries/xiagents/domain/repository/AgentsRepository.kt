package br.com.ximendesindustries.xiagents.domain.repository

import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.model.AgentMessage
import kotlinx.coroutines.flow.Flow

interface AgentsRepository {
    fun getAgents(): Flow<Result<List<Agent>>>
    fun sendMessage(agentId: String, message: String): Flow<Result<AgentMessage>>
}
