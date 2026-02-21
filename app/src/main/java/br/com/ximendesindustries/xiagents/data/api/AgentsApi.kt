package br.com.ximendesindustries.xiagents.data.api

import br.com.ximendesindustries.xiagents.data.model.request.ChatRequest
import br.com.ximendesindustries.xiagents.data.model.response.AgentResponse
import br.com.ximendesindustries.xiagents.data.model.response.ChatResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AgentsApi {

    @GET("/agents")
    suspend fun getAgents(): List<AgentResponse>

    @POST("/agents/chat/pixel")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): ChatResponse
}
