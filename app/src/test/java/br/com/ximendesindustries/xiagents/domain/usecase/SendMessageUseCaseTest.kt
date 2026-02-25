package br.com.ximendesindustries.xiagents.domain.usecase

import br.com.ximendesindustries.xiagents.MainDispatcherRule
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.AgentMessage
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SendMessageUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = mockk<AgentsRepository>()
    private val useCase = SendMessageUseCase(repository)

    @Test
    fun `invoke should return agent message from repository`() = runTest {
        val agentId = "pixel"
        val sessionId = "session-123"
        val message = "Olá, tudo bem?"
        val agentMessage = AgentMessage(
            agentId = agentId,
            sessionId = sessionId,
            message = "Olá! Tudo ótimo, obrigado!"
        )
        coEvery {
            repository.sendMessage(agentId, sessionId, message)
        } returns flowOf(Result.Success(agentMessage))

        val result = useCase.invoke(agentId, sessionId, message).let { flow ->
            var emitted: Result<AgentMessage>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(Result.Success(agentMessage), result)
        coVerify(exactly = 1) { repository.sendMessage(agentId, sessionId, message) }
    }

    @Test
    fun `invoke should handle null sessionId for new conversation`() = runTest {
        val agentId = "axel"
        val message = "Primeira mensagem"
        val agentMessage = AgentMessage(
            agentId = agentId,
            sessionId = "new-session-id",
            message = "Resposta do agente"
        )
        coEvery {
            repository.sendMessage(agentId, null, message)
        } returns flowOf(Result.Success(agentMessage))

        val result = useCase.invoke(agentId, null, message).let { flow ->
            var emitted: Result<AgentMessage>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(Result.Success(agentMessage), result)
        coVerify(exactly = 1) { repository.sendMessage(agentId, null, message) }
    }

    @Test
    fun `invoke should propagate error from repository`() = runTest {
        val agentId = "pixel"
        val sessionId = "session-123"
        val message = "Teste"
        val error = Result.Error("Network error")
        coEvery {
            repository.sendMessage(agentId, sessionId, message)
        } returns flowOf(error)

        val result = useCase.invoke(agentId, sessionId, message).let { flow ->
            var emitted: Result<AgentMessage>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(error, result)
        coVerify(exactly = 1) { repository.sendMessage(agentId, sessionId, message) }
    }
}
