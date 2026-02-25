package br.com.ximendesindustries.xiagents.domain.usecase

import br.com.ximendesindustries.xiagents.MainDispatcherRule
import br.com.ximendesindustries.xiagents.core.util.Result
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.domain.repository.AgentsRepository
import br.com.ximendesindustries.xiagents.factory.AgentFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class GetAgentsUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository = mockk<AgentsRepository>()
    private val useCase = GetAgentsUseCase(repository)

    @Test
    fun `invoke should return agents from repository`() = runTest {
        val agents = AgentFactory.simpleList()
        coEvery { repository.getAgents() } returns flowOf(Result.Success(agents))

        val result = useCase.invoke().let { flow ->
            var emitted: Result<List<Agent>>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(Result.Success(agents), result)
        coVerify(exactly = 1) { repository.getAgents() }
    }

    @Test
    fun `when invoke should propagate error from repository`() = runTest {
        val error = Result.Error("Network error")
        coEvery { repository.getAgents() } returns flowOf(error)

        val result = useCase.invoke().let { flow ->
            var emitted: Result<List<Agent>>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(error, result)
        coVerify(exactly = 1) { repository.getAgents() }
    }

    @Test
    fun `invoke should propagate loading from repository`() = runTest {
        coEvery { repository.getAgents() } returns flowOf(Result.Loading)

        val result = useCase.invoke().let { flow ->
            var emitted: Result<List<Agent>>? = null
            flow.collect { emitted = it }
            emitted
        }

        assertEquals(Result.Loading, result)
        coVerify(exactly = 1) { repository.getAgents() }
    }
}
