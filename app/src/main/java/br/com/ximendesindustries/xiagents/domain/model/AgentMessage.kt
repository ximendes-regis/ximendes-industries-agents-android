package br.com.ximendesindustries.xiagents.domain.model

data class AgentMessage(
    val agentId: String,
    val sessionId: String?,
    val message: String
)
