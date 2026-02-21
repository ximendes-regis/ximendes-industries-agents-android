package br.com.ximendesindustries.xiagents.data.api

import br.com.ximendesindustries.xiagents.data.model.request.ChatRequest
import br.com.ximendesindustries.xiagents.data.model.response.AgentResponse
import br.com.ximendesindustries.xiagents.data.model.response.ChatResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationDetailResponse
import br.com.ximendesindustries.xiagents.data.model.response.ConversationListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.POST

interface AgentsApi {

    @GET("/agents")
    suspend fun getAgents(): List<AgentResponse>

    @POST("/agents/chat/pixel")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): ChatResponse

    @GET("/agents/chat/pixel/sessions")
    suspend fun getPixelSessions(): ConversationListResponse

    @GET("/agents/chat/pixel/sessions/{id}")
    suspend fun getPixelSessionById(@Path("id") id: String): ConversationDetailResponse
}
