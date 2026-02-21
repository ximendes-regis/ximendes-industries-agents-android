package br.com.ximendesindustries.xiagents.data.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO da API: um turno da conversa (GET /agents/chat/pixel/sessions/{id}).
 */
@JsonClass(generateAdapter = true)
data class TurnResponse(
    @field:Json(name = "user_message") val userMessage: String,
    @field:Json(name = "assistant_message") val assistantMessage: String,
    @field:Json(name = "created_at") val createdAt: Long
)

/**
 * DTO da API: detalhe de uma sessão com todos os turnos (GET /agents/chat/pixel/sessions/{id}).
 */
@JsonClass(generateAdapter = true)
data class ConversationDetailResponse(
    @field:Json(name = "session_id") val sessionId: String,
    @field:Json(name = "session_name") val sessionName: String? = null,
    @field:Json(name = "created_at") val createdAt: Long,
    val turns: List<TurnResponse>
)
