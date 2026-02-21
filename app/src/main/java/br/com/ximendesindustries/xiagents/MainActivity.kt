package br.com.ximendesindustries.xiagents

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.com.ximendesindustries.xiagents.ui.screen.agentchat.AgentChatScreen
import br.com.ximendesindustries.xiagents.ui.screen.agentsmenu.AgentsMenuScreen
import br.com.ximendesindustries.xiagents.ui.theme.XiAgentsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XiAgentsTheme {
                XiAgentsApp()
            }
        }
    }
}

@Composable
fun XiAgentsApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            AgentsMenuScreen(
                modifier = Modifier.fillMaxSize(),
                onAgentClick = { agentId ->
                    navController.navigate("chat/$agentId")
                }
            )
        }
        
        composable(
            route = "chat/{agentId}",
            arguments = listOf(navArgument("agentId") { type = NavType.StringType })
        ) {
            AgentChatScreen(
                modifier = Modifier.fillMaxSize(),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
