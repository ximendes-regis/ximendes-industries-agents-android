package br.com.ximendesindustries.xiagents.domain.repository

import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.model.AgentMessage
import br.com.ximendesindustries.xiagents.domain.model.ChatSession
import br.com.ximendesindustries.xiagents.domain.model.SessionDetail
import kotlinx.coroutines.flow.Flow

interface AgentsRepository {
    fun getAgents(): Flow<Result<List<Agent>>>
    fun getSessions(agentId: String): Flow<Result<List<ChatSession>>>
    fun getSessionDetail(sessionId: String): Flow<Result<SessionDetail>>
    fun sendMessage(agentId: String, sessionId: String?, message: String): Flow<Result<AgentMessage>>
}
