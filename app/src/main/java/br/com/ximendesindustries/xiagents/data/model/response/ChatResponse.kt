package br.com.ximendesindustries.xiagents.data.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatResponse(
    @field:Json(name = "agent_id") val agentId: String,
    val message: String
)