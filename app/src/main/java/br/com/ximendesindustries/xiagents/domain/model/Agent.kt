package br.com.ximendesindustries.xiagents.domain.model

data class Agent(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String? = null
)
