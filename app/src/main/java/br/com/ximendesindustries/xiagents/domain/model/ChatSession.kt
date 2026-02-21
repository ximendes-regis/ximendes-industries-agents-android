package br.com.ximendesindustries.xiagents.domain.model

/**
 * Representa uma sessão de conversa com um agente.
 * Usado para listar e escolher com qual conversa continuar.
 */
data class ChatSession(
    val id: String,
    val agentId: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)
