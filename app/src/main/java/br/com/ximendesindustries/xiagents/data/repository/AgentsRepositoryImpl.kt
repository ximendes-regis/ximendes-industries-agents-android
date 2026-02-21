package br.com.ximendesindustries.xiagents.data.repository

import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.core.util.safeApiCall
import br.com.ximendesindustries.xiagents.data.datasource.AgentsRemoteDataSource
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.model.AgentMessage
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

    override fun sendMessage(agentId: String, message: String): Flow<Result<AgentMessage>> = flow {
        val result = safeApiCall {
            val response = remoteDataSource.sendMessage(agentId, message)
            AgentMessage(
                agentId = response.agentId,
                message = response.message
            )
        }
        emit(result)
    }
}
