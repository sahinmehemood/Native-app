package com.hermes.android.data.source.local

import com.hermes.android.domain.model.Agent
import com.hermes.android.domain.model.ChatEvent
import com.hermes.android.domain.model.Skill
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random
import javax.inject.Inject

/**
 * Bundled offline data so the app is fully usable without a backend. The real
 * Ktor client is used when a connection is configured; this is the fallback.
 */
class MockLocalSource @Inject constructor() {
    fun getAgents(): List<Agent> = listOf(
        Agent("agent-hermes", "Hermes", "General-purpose agent for coding, research, and automation.", model = "claude-opus", provider = "anthropic"),
        Agent("agent-research", "Research", "Deep web research, summarization, and citations.", model = "gpt-4o", provider = "openai"),
        Agent("agent-coder", "Coder", "Specialized software-engineering agent.", model = "deepseek-coder", provider = "deepseek"),
        Agent("agent-writer", "Writer", "Long-form writing, editing, and tone control.", model = "command-r", provider = "cohere")
    )

    fun getSkills(): List<Skill> = listOf(
        Skill("skill-web", "web_search", "Search the web and scrape pages.", version = "1.2.0", category = "Web", author = "hermes"),
        Skill("skill-fs", "filesystem", "Read and write files safely.", version = "2.0.1", category = "System", author = "hermes"),
        Skill("skill-shell", "shell", "Run shell commands in a sandbox.", version = "1.0.4", category = "System", author = "hermes"),
        Skill("skill-git", "git", "Stage, diff, commit, and open PRs.", version = "1.5.0", category = "Dev", author = "hermes"),
        Skill("skill-email", "email", "Draft and send email.", version = "0.9.0", category = "Productivity", author = "hermes"),
        Skill("skill-calendar", "calendar", "Manage calendar events.", version = "0.4.2", category = "Productivity", author = "hermes")
    )

    fun streamReply(userMessage: String): Flow<ChatEvent> = flow {
        val sessionId = "default"
        emit(ChatEvent.MessageStart(sessionId, "New conversation"))
        delay(250)
        emit(ChatEvent.Thinking("Planning a response…"))
        delay(400)

        if (userMessage.contains("search", ignoreCase = true) || Random.nextBoolean()) {
            emit(ChatEvent.ToolStart("web_search", mapOf("query" to userMessage.take(40))))
            repeat(4) { i ->
                delay(180)
                emit(ChatEvent.ToolProgress("web_search", (i + 1) / 4f, "Fetching result ${i + 1}"))
            }
            emit(ChatEvent.ToolEnd("web_search", "Found 5 relevant sources."))
        }

        val reply = buildReply(userMessage)
        reply.chunked(8).forEach { chunk ->
            delay(26)
            emit(ChatEvent.MessageDelta(chunk))
        }
        delay(150)
        emit(
            ChatEvent.MessageEnd(
                sessionId,
                tokensIn = userMessage.length / 4,
                tokensOut = reply.length / 4,
                cost = 0.002
            )
        )
        emit(ChatEvent.Done(sessionId))
    }

    private fun buildReply(userMessage: String): String =
        "You said: \"${userMessage.take(120)}\". I'm the Hermes agent running on your Android device. " +
            "In a full deployment I stream tokens from your connected backend (local Termux or a remote API). " +
            "For now this is a high-fidelity offline simulation so the experience is fully usable without a server."
}
