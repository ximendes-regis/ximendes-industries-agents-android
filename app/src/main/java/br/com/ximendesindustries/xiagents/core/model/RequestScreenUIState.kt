package br.com.ximendesindustries.xiagents.core.model

sealed class RequestScreenUIState {
    object Loading : RequestScreenUIState()
    object Error : RequestScreenUIState()
    object Success : RequestScreenUIState()
}