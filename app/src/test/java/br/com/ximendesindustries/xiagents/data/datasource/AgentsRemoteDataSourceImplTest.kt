package br.com.ximendesindustries.xiagents.data.datasource

import br.com.ximendesindustries.xiagents.MainDispatcherRule
import br.com.ximendesindustries.xiagents.data.api.AgentsApi
import br.com.ximendesindustries.xiagents.data.model.response.AgentResponse
import br.com.ximendesindustries.xiagents.data.model.response.ChatResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationDetailResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationListResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationSummaryResponse
import br.com.ximendesindustries.xiagents.data.model.response.TurnResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AgentsRemoteDataSourceImplTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val api = mockk<AgentsApi>()
    private val dataSource = AgentsRemoteDataSourceImpl(api)

    @Test
    fun `getAgents should call api and return agents`() = runTest {
        val agents = listOf(
            AgentResponse(id = "pixel", name = "Pixel", description = "Assistente", iconUrl = "url1"),
            AgentResponse(id = "axel", name = "Axel", description = "Atendimento", iconUrl = "url2")
        )
        coEvery { api.getAgents() } returns agents

        val result = dataSource.getAgents()

        assertEquals(agents, result)
        coVerify(exactly = 1) { api.getAgents() }
    }

    @Test
    fun `getSessions should call api and return sessions`() = runTest {
        val sessionsResponse = ConversationListResponse(
            data = listOf(
                ConversationSummaryResponse(
                    sessionId = "s1",
                    sessionName = "Conversa 1",
                    createdAt = 1234567890L
                )
            ),
            page = 1,
            limit = 20,
            total = 1
        )
        coEvery { api.getPixelSessions() } returns sessionsResponse

        val result = dataSource.getSessions()

        assertEquals(sessionsResponse, result)
        assertEquals(1, result.data.size)
        assertEquals("s1", result.data[0].sessionId)
        coVerify(exactly = 1) { api.getPixelSessions() }
    }

    @Test
    fun `getSessionDetail should call api with sessionId and return detail`() = runTest {
        val sessionId = "session-123"
        val detailResponse = ConversationDetailResponse(
            sessionId = sessionId,
            sessionName = "Minha conversa",
            createdAt = 1234567890L,
            turns = listOf(
                TurnResponse(
                    userMessage = "Olá",
                    assistantMessage = "Oi!",
                    createdAt = 1234567891L
                )
            )
        )
        coEvery { api.getPixelSessionById(sessionId) } returns detailResponse

        val result = dataSource.getSessionDetail(sessionId)

        assertEquals(detailResponse, result)
        assertEquals(1, result.turns.size)
        coVerify(exactly = 1) { api.getPixelSessionById(sessionId) }
    }

    @Test
    fun `sendMessage should call api and return response`() = runTest {
        val agentId = "pixel"
        val sessionId = "session-123"
        val message = "Olá!"
        val chatResponse = ChatResponse(
            agentId = agentId,
            sessionId = sessionId,
            message = "Resposta do Pixel"
        )
        coEvery { api.sendMessage(agentId, any()) } returns chatResponse

        val result = dataSource.sendMessage(agentId, sessionId, message)

        assertEquals(chatResponse, result)
        coVerify(exactly = 1) { api.sendMessage(agentId, any()) }
    }

    @Test
    fun `sendMessage with null sessionId should call api`() = runTest {
        val agentId = "pixel"
        val message = "Primeira mensagem"
        val chatResponse = ChatResponse(
            agentId = agentId,
            sessionId = "new-session",
            message = "Resposta"
        )
        coEvery { api.sendMessage(agentId, any()) } returns chatResponse

        val result = dataSource.sendMessage(agentId, null, message)

        assertEquals(chatResponse, result)
        coVerify(exactly = 1) { api.sendMessage(agentId, any()) }
    }
}
