package br.com.ximendesindustries.xiagents.data.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatRequest(
    @field:Json(name = "agent_id") val agentId: String,
    @field:Json(name = "session_id") val sessionId: String,
    val message: String
)