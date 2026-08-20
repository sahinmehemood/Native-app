package com.hermes.android.data.api

import com.hermes.android.data.api.dto.ChatRequestDto
import com.hermes.android.data.api.dto.ProfileDto
import com.hermes.android.data.api.dto.SkillDto
import com.hermes.android.domain.model.ConnectionConfig
import com.hermes.android.domain.repository.ConnectionRepository
import com.hermes.android.util.HermesException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class HermesApiService @Inject constructor(
    private val client: HttpClient,
    private val connectionRepository: ConnectionRepository
) {
    private suspend fun config(): ConnectionConfig =
        connectionRepository.observe().first()
            ?: throw HermesException("Hermes is not configured", kind = HermesException.Kind.NotFound)

    suspend fun getProfiles(): List<ProfileDto> {
        val c = config()
        return client.get("${c.baseUrl}/api/v1/profiles") { withAuth(c) }.body()
    }

    suspend fun getSkills(): List<SkillDto> {
        val c = config()
        return client.get("${c.baseUrl}/api/v1/skills") { withAuth(c) }.body()
    }

    suspend fun postChat(request: ChatRequestDto): HttpResponse {
        val c = config()
        return client.post("${c.baseUrl}/api/v1/chat") {
            withAuth(c)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
    }

    private fun HttpRequestBuilder.withAuth(c: ConnectionConfig) {
        if (c.apiKey.isNotBlank()) bearerAuth(c.apiKey)
    }
}
