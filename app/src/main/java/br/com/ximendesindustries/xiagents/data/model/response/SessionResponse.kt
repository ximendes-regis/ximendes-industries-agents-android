package br.com.ximendesindustries.xiagents.data.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * DTO da API: resumo de uma conversa (GET /pixel/sessions).
 * Não usar na UI/domínio — use [br.com.ximendesindustries.xiagents.domain.model.ChatSession].
 */
@JsonClass(generateAdapter = true)
data class ConversationSummaryResponse(
    @field:Json(name = "session_id") val sessionId: String,
    @field:Json(name = "session_name") val sessionName: String? = null,
    @field:Json(name = "created_at") val createdAt: Long,
    @field:Json(name = "runs_count") val runsCount: Int = 0
)

/**
 * DTO da API: resposta da listagem de sessões (GET /pixel/sessions).
 */
@JsonClass(generateAdapter = true)
data class ConversationListResponse(
    val data: List<ConversationSummaryResponse>,
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0
)
