package br.com.ximendesindustries.xiagents.domain.usecase

import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.ChatSession
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionsUseCase @Inject constructor(
    private val repository: AgentsRepository
) {
    suspend operator fun invoke(agentId: String): Flow<Result<List<ChatSession>>> =
        repository.getSessions(agentId)
}
