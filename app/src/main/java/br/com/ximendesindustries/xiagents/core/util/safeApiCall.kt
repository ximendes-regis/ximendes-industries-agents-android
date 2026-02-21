package br.com.ximendesindustries.xiagents.core.util

import android.util.Log
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException

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
                Log.e("SafeApiCall", "HTTP Error $code: $message\nResponse: $responseBody")
                "Erro HTTP $code: ${message ?: "Erro desconhecido"}"
            }
            is JsonDataException -> {
                Log.e("SafeApiCall", "JSON Parsing Error: ${e.message}", e)
                "Erro ao processar resposta do servidor: ${e.message ?: "Formato de dados inválido"}"
            }
            is JsonEncodingException -> {
                Log.e("SafeApiCall", "JSON Encoding Error: ${e.message}", e)
                "Erro ao processar resposta do servidor: ${e.message ?: "Formato de dados inválido"}"
            }
            is com.squareup.moshi.JsonEncodingException -> {
                Log.e("SafeApiCall", "Moshi JSON Error: ${e.message}", e)
                "Erro ao processar resposta do servidor: ${e.message ?: "Formato de dados inválido"}"
            }
            else -> {
                Log.e("SafeApiCall", "Unexpected Error: ${e.javaClass.simpleName}", e)
                e.localizedMessage ?: e.message ?: "Erro desconhecido: ${e.javaClass.simpleName}"
            }
        }
        Result.Error(errorMessage, e)
    }
}
