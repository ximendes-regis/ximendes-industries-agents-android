package br.com.ximendesindustries.xiagents.domain.model

/**
 * Detalhe de uma sessão de chat: lista de turnos (par user/assistant).
 * Usado ao carregar uma conversa ao selecionar uma sessão.
 */
data class SessionDetail(
    val sessionId: String,
    val sessionName: String?,
    val createdAt: Long,
    val turns: List<SessionTurn>
)

/**
 * Um turno da conversa: mensagem do usuário e resposta do assistente.
 */
data class SessionTurn(
    val userMessage: String,
    val assistantMessage: String,
    val createdAt: Long
)
