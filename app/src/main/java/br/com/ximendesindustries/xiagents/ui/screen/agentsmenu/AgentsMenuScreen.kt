package br.com.ximendesindustries.xiagents.ui.screen.agentsmenu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.com.ximendesindustries.xiagents.core.model.RequestUIState
import br.com.ximendesindustries.xiagents.domain.model.Agent
import br.com.ximendesindustries.xiagents.ui.screen.agentsmenu.components.AgentCard
import br.com.ximendesindustries.xiagents.ui.screen.agentsmenu.model.AgentsMenuViewModelAction
import br.com.ximendesindustries.xiagents.ui.theme.XiAgentsTheme
import br.com.ximendesindustries.xiagents.ui.theme.audioWide
import br.com.ximendesindustries.xiagents.ui.theme.mostWastedFont

@Composable
fun AgentsMenuScreen(
    modifier: Modifier = Modifier,
    viewModel: AgentsMenuViewModel = hiltViewModel(),
    onAgentClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.performAction(AgentsMenuViewModelAction.StartAction)
    }

    AgentsMenuContent(
        uiState = uiState,
        onAgentClick = onAgentClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentsMenuContent(
    uiState: AgentsMenuUiState,
    onAgentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text("Ximendes", fontFamily = audioWide)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            "Industries",
                            fontFamily = mostWastedFont,
                            fontSize = 24.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                RequestUIState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                RequestUIState.Error -> {
                    Text(
                        text = "Algo de errado ocorre",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                RequestUIState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.agents) { agent ->
                            AgentCard(
                                agent = agent,
                                onClick = { onAgentClick(agent.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun AgentsMenuScreenPreview() {
    XiAgentsTheme {
        AgentsMenuContent(
            uiState = AgentsMenuUiState(
                requestUIState = RequestUIState.Success,
                agents = listOf(
                    Agent("1", "Agente 1", "Descrição do agente 1"),
                    Agent("2", "Agente 2", "Descrição do agente 2")
                )
            ),
            onAgentClick = {}
        )
    }
}
