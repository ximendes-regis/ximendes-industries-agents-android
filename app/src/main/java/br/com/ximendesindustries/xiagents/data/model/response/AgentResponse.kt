package br.com.ximendesindustries.xiagents.data.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentResponse(
    val id: String,
    val name: String,
    val description: String,
    @field:Json(name = "icon_url") val iconUrl: String? = null
)
