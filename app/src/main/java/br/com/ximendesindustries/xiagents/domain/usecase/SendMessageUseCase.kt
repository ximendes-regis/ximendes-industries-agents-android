package br.com.ximendesindustries.xiagents.domain.usecase

import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.AgentMessage
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: AgentsRepository
) {
    suspend operator fun invoke(agentId: String, sessionId: String?, message: String): Flow<Result<AgentMessage>> {
        return repository.sendMessage(agentId, sessionId, message)
    }
}
