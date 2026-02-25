package br.com.ximendesindustries.xiagents.ui.screen.agentchat

import androidx.lifecycle.SavedStateHandle
import br.com.ximendesindustries.xiagents.MainDispatcherRule
import br.com.ximendesindustries.xiagents.core.model.RequestUIState
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.AgentMessage
import br.com.ximendesindustries.xiagents.domain.model.ChatSession
import br.com.ximendesindustries.xiagents.domain.model.SessionDetail
import br.com.ximendesindustries.xiagents.domain.model.SessionTurn
import br.com.ximendesindustries.xiagents.domain.usecase.GetSessionDetailUseCase
import br.com.ximendesindustries.xiagents.domain.usecase.GetSessionsUseCase
import br.com.ximendesindustries.xiagents.domain.usecase.SendMessageUseCase
import br.com.ximendesindustries.xiagents.ui.screen.agentchat.model.AgentChatViewModelAction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AgentChatViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val savedStateHandle = mockk<SavedStateHandle>()
    private val getSessionsUseCase = mockk<GetSessionsUseCase>()
    private val getSessionDetailUseCase = mockk<GetSessionDetailUseCase>()
    private val sendMessageUseCase = mockk<SendMessageUseCase>()

    private fun createViewModel(agentId: String = "axel"): AgentChatViewModel {
        every { savedStateHandle.get<String>("agentId") } returns agentId
        return AgentChatViewModel(
            savedStateHandle = savedStateHandle,
            getSessionsUseCase = getSessionsUseCase,
            getSessionDetailUseCase = getSessionDetailUseCase,
            sendMessageUseCase = sendMessageUseCase
        )
    }

    @Test
    fun `when StartAction and agent is not pixel, should set Success with welcome message and not load sessions`() = runTest {
        every { savedStateHandle.get<String>("agentId") } returns "axel"
        val viewModel = AgentChatViewModel(
            savedStateHandle = savedStateHandle,
            getSessionsUseCase = getSessionsUseCase,
            getSessionDetailUseCase = getSessionDetailUseCase,
            sendMessageUseCase = sendMessageUseCase
        )

        viewModel.performAction(AgentChatViewModelAction.StartAction)

        assertEquals(RequestUIState.Success, viewModel.uiState.value.requestUIState)
        assertEquals("axel", viewModel.uiState.value.agentName)
        assertEquals(1, viewModel.uiState.value.messages.size)
        assertEquals("Olá Sr. Ximendes", viewModel.uiState.value.messages[0].content)
        assertFalse(viewModel.uiState.value.messages[0].isFromUser)
        assertTrue(viewModel.uiState.value.sessions.isEmpty())
        coVerify(exactly = 0) { getSessionsUseCase(any()) }
    }

    @Test
    fun `when StartAction and agent is pixel, should load sessions and update state on success`() = runTest {
        every { savedStateHandle.get<String>("agentId") } returns "pixel"
        val sessions = listOf(
            ChatSession(id = "s1", agentId = "pixel", title = "Conversa 1"),
            ChatSession(id = "s2", agentId = "pixel", title = "Conversa 2")
        )
        coEvery { getSessionsUseCase("pixel") } returns flowOf(Result.Success(sessions))

        val viewModel = AgentChatViewModel(
            savedStateHandle = savedStateHandle,
            getSessionsUseCase = getSessionsUseCase,
            getSessionDetailUseCase = getSessionDetailUseCase,
            sendMessageUseCase = sendMessageUseCase
        )

        viewModel.performAction(AgentChatViewModelAction.StartAction)

        assertEquals(RequestUIState.Success, viewModel.uiState.value.requestUIState)
        assertEquals("pixel", viewModel.uiState.value.agentName)
        assertEquals(1, viewModel.uiState.value.messages.size)
        assertEquals(2, viewModel.uiState.value.sessions.size)
        assertEquals("s1", viewModel.uiState.value.sessions[0].id)
        assertEquals("Conversa 1", viewModel.uiState.value.sessions[0].title)
        coVerify(exactly = 1) { getSessionsUseCase("pixel") }
    }

    @Test
    fun `when SelectSessionAction null, should set selectedSession null and messages to welcome only`() = runTest {
        val viewModel = createViewModel()
        viewModel.performAction(AgentChatViewModelAction.StartAction)
        assertEquals(1, viewModel.uiState.value.messages.size)

        viewModel.performAction(AgentChatViewModelAction.SelectSessionAction(null))

        assertEquals(null, viewModel.uiState.value.selectedSession)
        assertEquals(1, viewModel.uiState.value.messages.size)
        assertEquals("Olá Sr. Ximendes", viewModel.uiState.value.messages[0].content)
    }

    @Test
    fun `when SelectSessionAction with session without cache, should set isLoadingSessionDetail and call getSessionDetailUseCase`() = runTest {
        coEvery { getSessionDetailUseCase("s1") } returns flowOf(
            Result.Success(
                SessionDetail(
                    sessionId = "s1",
                    sessionName = null,
                    createdAt = 0L,
                    turns = emptyList()
                )
            )
        )
        val viewModel = createViewModel()
        viewModel.performAction(AgentChatViewModelAction.StartAction)
        val session = ChatSession(id = "s1", agentId = "axel", title = "Sessão 1")

        viewModel.performAction(AgentChatViewModelAction.SelectSessionAction(session))

        assertTrue(viewModel.uiState.value.messages.isNotEmpty())
        assertEquals("s1", viewModel.uiState.value.selectedSession?.id)
        coVerify(exactly = 1) { getSessionDetailUseCase("s1") }
    }

    @Test
    fun `when SelectSessionAction with session then select again, should use cache and not call getSessionDetailUseCase again`() = runTest {
        coEvery { getSessionDetailUseCase("s1") } returns flowOf(
            Result.Success(
                SessionDetail(
                    sessionId = "s1",
                    sessionName = "Sessão 1",
                    createdAt = 0L,
                    turns = listOf(
                        SessionTurn("Oi", "Olá!", 0L)
                    )
                )
            )
        )
        val viewModel = createViewModel()
        viewModel.performAction(AgentChatViewModelAction.StartAction)
        val session = ChatSession(id = "s1", agentId = "axel", title = "Sessão 1")

        viewModel.performAction(AgentChatViewModelAction.SelectSessionAction(session))
        viewModel.performAction(AgentChatViewModelAction.SelectSessionAction(null))
        viewModel.performAction(AgentChatViewModelAction.SelectSessionAction(session))

        assertEquals("s1", viewModel.uiState.value.selectedSession?.id)
        coVerify(exactly = 1) { getSessionDetailUseCase("s1") }
    }

    @Test
    fun `when SendMessageAction with non blank text, should add user message and call sendMessageUseCase`() = runTest {
        coEvery { sendMessageUseCase("axel", null, "Olá") } returns flowOf(
            Result.Success(AgentMessage(agentId = "axel", sessionId = null, message = "Oi!"))
        )
        val viewModel = createViewModel()
        viewModel.performAction(AgentChatViewModelAction.StartAction)
        val initialCount = viewModel.uiState.value.messages.size

        viewModel.performAction(AgentChatViewModelAction.SendMessageAction("Olá"))

        assertTrue(viewModel.uiState.value.messages.size >= initialCount + 1)
        assertTrue(viewModel.uiState.value.messages.any { it.content == "Olá" && it.isFromUser })
        coVerify(exactly = 1) { sendMessageUseCase("axel", null, "Olá") }
    }

    @Test
    fun `when SendMessageAction with blank text, should not call sendMessageUseCase`() = runTest {
        val viewModel = createViewModel()
        viewModel.performAction(AgentChatViewModelAction.StartAction)

        viewModel.performAction(AgentChatViewModelAction.SendMessageAction("   "))
        viewModel.performAction(AgentChatViewModelAction.SendMessageAction(""))

        coVerify(exactly = 0) { sendMessageUseCase(any(), any(), any()) }
    }

    @Test
    fun `when SendMessageAction and use case returns error, should add error message to state`() = runTest {
        coEvery { sendMessageUseCase("axel", null, "Olá") } returns flowOf(
            Result.Error("Falha na rede", null)
        )
        val viewModel = createViewModel()
        viewModel.performAction(AgentChatViewModelAction.StartAction)

        viewModel.performAction(AgentChatViewModelAction.SendMessageAction("Olá"))

        assertTrue(
            viewModel.uiState.value.messages.any { it.content.contains("Erro ao enviar") && !it.isFromUser }
        )
        coVerify(exactly = 1) { sendMessageUseCase("axel", null, "Olá") }
    }

    @Test
    fun `when SendMessageAction and use case returns success, should add agent message to state`() = runTest {
        coEvery { sendMessageUseCase("axel", null, "Olá") } returns flowOf(
            Result.Success(AgentMessage(agentId = "axel", sessionId = null, message = "Como posso ajudar?"))
        )
        val viewModel = createViewModel()
        viewModel.performAction(AgentChatViewModelAction.StartAction)

        viewModel.performAction(AgentChatViewModelAction.SendMessageAction("Olá"))

        assertTrue(
            viewModel.uiState.value.messages.any { it.content == "Como posso ajudar?" && !it.isFromUser }
        )
        coVerify(exactly = 1) { sendMessageUseCase("axel", null, "Olá") }
    }
}
