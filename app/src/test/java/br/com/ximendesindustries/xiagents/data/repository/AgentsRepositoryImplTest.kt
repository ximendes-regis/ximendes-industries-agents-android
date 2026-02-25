package br.com.ximendesindustries.xiagents.data.repository

import br.com.ximendesindustries.xiagents.MainDispatcherRule
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.data.datasource.AgentsRemoteDataSource
import br.com.ximendesindustries.xiagents.data.model.response.AgentResponse
import br.com.ximendesindustries.xiagents.data.model.response.ChatResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationDetailResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationListResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationSummaryResponse
import br.com.ximendesindustries.xiagents.data.model.response.TurnResponse
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.model.AgentMessage
import br.com.ximendesindustries.xiagents.domain.model.ChatSession
import br.com.ximendesindustries.xiagents.domain.model.SessionDetail
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AgentsRepositoryImplTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val remoteDataSource = mockk<AgentsRemoteDataSource>()
    private val repository = AgentsRepositoryImpl(remoteDataSource)

    @Test
    fun `getAgents should map AgentResponse to Agent and return Success`() = runTest {
        val agentResponses = listOf(
            AgentResponse(id = "pixel", name = "Pixel", description = "Assistente", iconUrl = "url1"),
            AgentResponse(id = "axel", name = "Axel", description = "Atendimento", iconUrl = null)
        )
        coEvery { remoteDataSource.getAgents() } returns agentResponses

        val result = repository.getAgents().first()

        assertTrue(result is Result.Success)
        val agents = (result as Result.Success).data
        assertEquals(2, agents.size)
        assertEquals(Agent("pixel", "Pixel", "Assistente", "url1"), agents[0])
        assertEquals(Agent("axel", "Axel", "Atendimento", null), agents[1])
        coVerify(exactly = 1) { remoteDataSource.getAgents() }
    }

    @Test
    fun `getAgents should return Error when data source throws`() = runTest {
        coEvery { remoteDataSource.getAgents() } throws RuntimeException("Network error")

        val result = repository.getAgents().first()

        assertTrue(result is Result.Error)
        assertEquals("Network error", (result as Result.Error).message)
    }

    @Test
    fun `getSessions should map response to ChatSession and return Success`() = runTest {
        val agentId = "pixel"
        val listResponse = ConversationListResponse(
            data = listOf(
                ConversationSummaryResponse(
                    sessionId = "s1",
                    sessionName = "Conversa 1",
                    createdAt = 1234567890L
                ),
                ConversationSummaryResponse(
                    sessionId = "s2",
                    sessionName = "",
                    createdAt = 1234567891L
                )
            )
        )
        coEvery { remoteDataSource.getSessions() } returns listResponse

        val result = repository.getSessions(agentId).first()

        assertTrue(result is Result.Success)
        val sessions = (result as Result.Success).data
        assertEquals(2, sessions.size)
        assertEquals(
            ChatSession(id = "s1", agentId = agentId, title = "Conversa 1", createdAt = 1234567890L),
            sessions[0]
        )
        assertEquals(
            ChatSession(id = "s2", agentId = agentId, title = "s2", createdAt = 1234567891L),
            sessions[1]
        )
        coVerify(exactly = 1) { remoteDataSource.getSessions() }
    }

    @Test
    fun `getSessions should return Error when data source throws`() = runTest {
        coEvery { remoteDataSource.getSessions() } throws Exception("API error")

        val result = repository.getSessions("pixel").first()

        assertTrue(result is Result.Error)
    }

    @Test
    fun `getSessionDetail should map response to SessionDetail and return Success`() = runTest {
        val sessionId = "session-123"
        val detailResponse = ConversationDetailResponse(
            sessionId = sessionId,
            sessionName = "Minha conversa",
            createdAt = 1234567890L,
            turns = listOf(
                TurnResponse(
                    userMessage = "Olá",
                    assistantMessage = "Oi! Como posso ajudar?",
                    createdAt = 1234567891L
                )
            )
        )
        coEvery { remoteDataSource.getSessionDetail(sessionId) } returns detailResponse

        val result = repository.getSessionDetail(sessionId).first()

        assertTrue(result is Result.Success)
        val detail = (result as Result.Success).data
        assertEquals(sessionId, detail.sessionId)
        assertEquals("Minha conversa", detail.sessionName)
        assertEquals(1234567890L, detail.createdAt)
        assertEquals(1, detail.turns.size)
        assertEquals("Olá", detail.turns[0].userMessage)
        assertEquals("Oi! Como posso ajudar?", detail.turns[0].assistantMessage)
        coVerify(exactly = 1) { remoteDataSource.getSessionDetail(sessionId) }
    }

    @Test
    fun `getSessionDetail should return Error when data source throws`() = runTest {
        coEvery { remoteDataSource.getSessionDetail(any()) } throws Exception("Not found")

        val result = repository.getSessionDetail("invalid").first()

        assertTrue(result is Result.Error)
    }

    @Test
    fun `sendMessage should map response to AgentMessage and return Success`() = runTest {
        val agentId = "pixel"
        val sessionId = "session-123"
        val message = "Olá!"
        val chatResponse = ChatResponse(
            agentId = agentId,
            sessionId = sessionId,
            message = "Resposta do Pixel"
        )
        coEvery {
            remoteDataSource.sendMessage(agentId, sessionId, message)
        } returns chatResponse

        val result = repository.sendMessage(agentId, sessionId, message).first()

        assertTrue(result is Result.Success)
        val agentMessage = (result as Result.Success).data
        assertEquals(
            AgentMessage(agentId = agentId, sessionId = sessionId, message = "Resposta do Pixel"),
            agentMessage
        )
        coVerify(exactly = 1) { remoteDataSource.sendMessage(agentId, sessionId, message) }
    }

    @Test
    fun `sendMessage with blank sessionId in response should map to null`() = runTest {
        val agentId = "pixel"
        val chatResponse = ChatResponse(
            agentId = agentId,
            sessionId = "",
            message = "Resposta"
        )
        coEvery { remoteDataSource.sendMessage(agentId, null, any()) } returns chatResponse

        val result = repository.sendMessage(agentId, null, "Oi").first()

        assertTrue(result is Result.Success)
        assertNull((result as Result.Success).data.sessionId)
    }

    @Test
    fun `sendMessage should return Error when data source throws`() = runTest {
        coEvery {
            remoteDataSource.sendMessage(any(), any(), any())
        } throws Exception("Network error")

        val result = repository.sendMessage("pixel", "s1", "msg").first()

        assertTrue(result is Result.Error)
    }
}
