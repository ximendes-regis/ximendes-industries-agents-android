package br.com.ximendesindustries.xiagents.ui.screen.agentsmenu

import br.com.ximendesindustries.xiagents.MainDispatcherRule
import br.com.ximendesindustries.xiagents.core.model.RequestUIState
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.usecase.GetAgentsUseCase
import br.com.ximendesindustries.xiagents.factory.AgentFactory
import br.com.ximendesindustries.xiagents.ui.screen.agentsmenu.model.AgentsMenuViewModelAction
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AgentsMenuViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val getAgentsUseCase = mockk<GetAgentsUseCase>()

    private val viewModel = AgentsMenuViewModel(
        getAgentsUseCase = getAgentsUseCase
    )

    @Test
    fun `when view model start, should load agents with success`() = runTest {
        prepareScenario()

        viewModel.performAction(AgentsMenuViewModelAction.StartAction)

        coVerify(exactly = 1) { getAgentsUseCase() }
        assertEquals(RequestUIState.Success, viewModel.uiState.value.requestUIState)
        assertTrue(viewModel.uiState.value.agents.isNotEmpty())
        assertEquals(2, viewModel.uiState.value.agents.size)
    }

    @Test
    fun `when view model start and use case returns loading, should show loading state`() = runTest {
        prepareScenario(result = Result.Loading)

        viewModel.performAction(AgentsMenuViewModelAction.StartAction)

        coVerify(exactly = 1) { getAgentsUseCase() }
        assertEquals(RequestUIState.Loading, viewModel.uiState.value.requestUIState)
        assertTrue(viewModel.uiState.value.agents.isEmpty())
    }

    @Test
    fun `when view model start and use case returns error, should show error state`() = runTest {
        prepareScenario(result = Result.Error("Falha ao carregar agentes", null))

        viewModel.performAction(AgentsMenuViewModelAction.StartAction)

        coVerify(exactly = 1) { getAgentsUseCase() }
        assertEquals(RequestUIState.Error, viewModel.uiState.value.requestUIState)
        assertTrue(viewModel.uiState.value.agents.isEmpty())
    }

    @Test
    fun `when view model start, should map domain agents to ui agents correctly`() = runTest {
        prepareScenario()

        viewModel.performAction(AgentsMenuViewModelAction.StartAction)

        val agents = viewModel.uiState.value.agents
        assertEquals("pixel", agents[0].id)
        assertEquals("Pixel", agents[0].name)
        assertEquals("Assistente de marketing da Ximendes Industries.", agents[0].description)
        assertEquals("axel", agents[1].id)
        assertEquals("Axel", agents[1].name)
    }

    private fun prepareScenario(
        result: Result<List<Agent>> = Result.Success(AgentFactory.simpleList())
    ) {
        coEvery { getAgentsUseCase() } returns flowOf(result)
    }
}