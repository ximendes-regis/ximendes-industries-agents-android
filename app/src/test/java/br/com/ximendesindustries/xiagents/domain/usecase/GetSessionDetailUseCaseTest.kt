package br.com.ximendesindustries.xiagents.domain.usecase

import br.com.ximendesindustries.xiagents.MainDispatcherRule
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.SessionDetail
import br.com.ximendesindustries.xiagents.domain.model.SessionTurn
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GetSessionDetailUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = mockk<AgentsRepository>()
    private val useCase = GetSessionDetailUseCase(repository)

    @Test
    fun `invoke should return session detail from repository with correct sessionId`() = runTest {
        val sessionId = "session-123"
        val sessionDetail = SessionDetail(
            sessionId = sessionId,
            sessionName = "Minha conversa",
            createdAt = 1234567890L,
            turns = listOf(
                SessionTurn(
                    userMessage = "Olá",
                    assistantMessage = "Oi! Como posso ajudar?",
                    createdAt = 1234567891L
                )
            )
        )
        coEvery { repository.getSessionDetail(sessionId) } returns flowOf(Result.Success(sessionDetail))

        val result = useCase.invoke(sessionId).let { flow ->
            var emitted: Result<SessionDetail>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(Result.Success(sessionDetail), result)
        coVerify(exactly = 1) { repository.getSessionDetail(sessionId) }
    }

    @Test
    fun `invoke should propagate error from repository`() = runTest {
        val sessionId = "session-123"
        val error = Result.Error("Session not found")
        coEvery { repository.getSessionDetail(sessionId) } returns flowOf(error)

        val result = useCase.invoke(sessionId).let { flow ->
            var emitted: Result<SessionDetail>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(error, result)
        coVerify(exactly = 1) { repository.getSessionDetail(sessionId) }
    }

    @Test
    fun `invoke should return empty turns when session has no messages`() = runTest {
        val sessionId = "empty-session"
        val sessionDetail = SessionDetail(
            sessionId = sessionId,
            sessionName = "Nova conversa",
            createdAt = 1234567890L,
            turns = emptyList()
        )
        coEvery { repository.getSessionDetail(sessionId) } returns flowOf(Result.Success(sessionDetail))

        val result = useCase.invoke(sessionId).let { flow ->
            var emitted: Result<SessionDetail>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(Result.Success(sessionDetail), result)
        assertEquals(0, (result as Result.Success).data.turns.size)
    }
}
