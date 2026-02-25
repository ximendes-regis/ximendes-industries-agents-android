package br.com.ximendesindustries.xiagents.core.util

import android.util.Log
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException

private fun safeLogError(tag: String, message: String, throwable: Throwable? = null) {
    try {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    } catch (_: Exception) {
        // Log não disponível em testes unitários (JVM)
    }
}

suspend fun <T> safeApiCall(
    apiCall: suspend () -> T
): Result<T> {
    return try {
        val result = apiCall()
        Result.Success(result)
    } catch (e: Exception) {
        val errorMessage = when (e) {
            is java.net.UnknownHostException -> "Sem conexão com a internet"
            is java.net.SocketTimeoutException -> "Tempo de conexão esgotado"
            is retrofit2.HttpException -> {
                val code = e.code()
                val message = e.message()
                val responseBody = e.response()?.errorBody()?.string()
                safeLogError("SafeApiCall", "HTTP Error $code: $message\nResponse: $responseBody")
                "Erro HTTP $code: ${message ?: "Erro desconhecido"}"
            }
            is JsonDataException -> {
                safeLogError("SafeApiCall", "JSON Parsing Error: ${e.message}", e)
                "Erro ao processar resposta do servidor: ${e.message ?: "Formato de dados inválido"}"
            }
            is JsonEncodingException -> {
                safeLogError("SafeApiCall", "JSON Encoding Error: ${e.message}", e)
                "Erro ao processar resposta do servidor: ${e.message ?: "Formato de dados inválido"}"
            }
            else -> {
                safeLogError("SafeApiCall", "Unexpected Error: ${e.javaClass.simpleName}", e)
                e.localizedMessage ?: e.message ?: "Erro desconhecido: ${e.javaClass.simpleName}"
            }
        }
        Result.Error(errorMessage, e)
    }
}
