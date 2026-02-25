package br.com.ximendesindustries.xiagents.domain.usecase

import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAgentsUseCase @Inject constructor(
    private val repository: AgentsRepository
) {
   suspend operator fun invoke(): Flow<Result<List<Agent>>> {
        return repository.getAgents()
    }
}
