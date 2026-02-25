package br.com.ximendesindustries.xiagents.factory

import br.com.ximendesindustries.xiagents.domain.model.Agent


object AgentFactory {

    fun create(
        id: String,
        name: String,
        description: String,
        iconUrl: String? = null
    ): Agent {
        return Agent(
            id = id.lowercase(),
            name = name.trim(),
            description = description.trim(),
            iconUrl = iconUrl
        )
    }

    fun pixel(): Agent {
        return Agent(
            id = "pixel",
            name = "Pixel",
            description = "Assistente de marketing da Ximendes Industries.",
            iconUrl = "https://example.com/pixel.png"
        )
    }

    fun axel(): Agent {
        return Agent(
            id = "axel",
            name = "Axel",
            description = "Assistente de atendimento da Ximendes Industries.",
            iconUrl = "https://example.com/axel.png"
        )
    }

    fun simpleList(): List<Agent> {
        return listOf(
            pixel(),
            axel()
        )
    }
}