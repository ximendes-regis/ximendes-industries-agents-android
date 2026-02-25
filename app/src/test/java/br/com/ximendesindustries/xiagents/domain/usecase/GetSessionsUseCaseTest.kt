package br.com.ximendesindustries.xiagents.domain.usecase

import br.com.ximendesindustries.xiagents.MainDispatcherRule
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.ChatSession
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GetSessionsUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = mockk<AgentsRepository>()
    private val useCase = GetSessionsUseCase(repository)

    @Test
    fun `invoke should return sessions from repository with correct agentId`() = runTest {
        val agentId = "pixel"
        val sessions = listOf(
            ChatSession(id = "s1", agentId = agentId, title = "Conversa 1"),
            ChatSession(id = "s2", agentId = agentId, title = "Conversa 2")
        )
        coEvery { repository.getSessions(agentId) } returns flowOf(Result.Success(sessions))

        val result = useCase.invoke(agentId).let { flow ->
            var emitted: Result<List<ChatSession>>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(Result.Success(sessions), result)
        coVerify(exactly = 1) { repository.getSessions(agentId) }
    }

    @Test
    fun `invoke should propagate error from repository`() = runTest {
        val agentId = "pixel"
        val error = Result.Error("Failed to load sessions")
        coEvery { repository.getSessions(agentId) } returns flowOf(error)

        val result = useCase.invoke(agentId).let { flow ->
            var emitted: Result<List<ChatSession>>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(error, result)
        coVerify(exactly = 1) { repository.getSessions(agentId) }
    }

    @Test
    fun `invoke should call repository with different agentIds`() = runTest {
        val agentId = "axel"
        coEvery { repository.getSessions(agentId) } returns flowOf(Result.Success(emptyList()))

        useCase.invoke(agentId).collect { }

        coVerify(exactly = 1) { repository.getSessions("axel") }
    }
}
