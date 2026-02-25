package br.com.ximendesindustries.xiagents.core.model

sealed class RequestUIState {
    object Loading : RequestUIState()
    object Error : RequestUIState()
    object Success : RequestUIState()
}