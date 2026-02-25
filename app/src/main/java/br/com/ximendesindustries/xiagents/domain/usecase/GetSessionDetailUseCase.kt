package br.com.ximendesindustries.xiagents.domain.usecase

import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.SessionDetail
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionDetailUseCase @Inject constructor(
    private val repository: AgentsRepository
) {
    suspend operator fun invoke(sessionId: String): Flow<Result<SessionDetail>> =
        repository.getSessionDetail(sessionId)
}
