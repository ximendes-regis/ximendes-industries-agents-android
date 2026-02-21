package br.com.ximendesindustries.xiagents.data.repository

import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.core.util.safeApiCall
import br.com.ximendesindustries.xiagents.data.datasource.AgentsRemoteDataSource
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.model.AgentMessage
import br.com.ximendesindustries.xiagents.domain.model.ChatSession
import br.com.ximendesindustries.xiagents.domain.model.SessionDetail
import br.com.ximendesindustries.xiagents.domain.model.SessionTurn
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AgentsRepositoryImpl @Inject constructor(
    private val remoteDataSource: AgentsRemoteDataSource
) : AgentsRepository {

    override fun getAgents(): Flow<Result<List<Agent>>> = flow {
        val result = safeApiCall {
            remoteDataSource.getAgents().map { response ->
                Agent(
                    id = response.id,
                    name = response.name,
                    description = response.description,
                    iconUrl = response.iconUrl
                )
            }
        }
        emit(result)
    }

    override fun getSessions(agentId: String): Flow<Result<List<ChatSession>>> = flow {
        val result = safeApiCall {
            remoteDataSource.getSessions().data.map { dto ->
                ChatSession(
                    id = dto.sessionId,
                    agentId = agentId,
                    title = dto.sessionName?.takeIf { it.isNotBlank() } ?: dto.sessionId,
                    createdAt = dto.createdAt
                )
            }
        }
        emit(result)
    }

    override fun getSessionDetail(sessionId: String): Flow<Result<SessionDetail>> = flow {
        val result = safeApiCall {
            val response = remoteDataSource.getSessionDetail(sessionId)
            SessionDetail(
                sessionId = response.sessionId,
                sessionName = response.sessionName,
                createdAt = response.createdAt,
                turns = response.turns.map { t ->
                    SessionTurn(
                        userMessage = t.userMessage,
                        assistantMessage = t.assistantMessage,
                        createdAt = t.createdAt
                    )
                }
            )
        }
        emit(result)
    }

    override fun sendMessage(agentId: String, sessionId: String?, message: String): Flow<Result<AgentMessage>> = flow {
        val result = safeApiCall {
            val response = remoteDataSource.sendMessage(agentId, sessionId, message)
            AgentMessage(
                agentId = response.agentId,
                sessionId = response.sessionId.takeIf { it.isNotBlank() },
                message = response.message
            )
        }
        emit(result)
    }
}
