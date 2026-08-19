# API_INTEGRATION.md — Hermes Android

## Base URLs
- Local Hermes (Termux): `http://127.0.0.1:8642`
- Remote: user-configured `https://host:port`
- Auth header: `Authorization: Bearer <api_key>`

## Endpoints (see master plan §4.2 for full table)
| Method+Path | Request | Response |
|-------------|---------|----------|
| GET /health | — | `{status, version}` |
| GET /api/v1/config | — | config object |
| POST /api/v1/config | partial | config |
| GET /api/v1/providers | — | providers+models |
| POST /api/v1/chat | `{session_id?, message, model?, provider?, profile?, stream:true}` | SSE |
| GET /api/v1/sessions | `?query=&page=&limit=` | `{sessions, total}` |
| GET /api/v1/sessions/{id} | — | session + messages |
| DELETE /api/v1/sessions/{id} | — | 204 |
| GET /api/v1/profiles | — | profiles |
| POST /api/v1/profiles | `{name, config}` | profile |
| DELETE /api/v1/profiles/{id} | — | 204 |
| GET /api/v1/skills | — | skills |
| POST /api/v1/skills/install | `{name, source}` | skill |
| GET /api/v1/models | — | models |
| POST /api/v1/models | `{name, provider, model_id, params}` | model |
| GET /api/v1/memory | — | memory entries |
| PUT /api/v1/memory | `{entries}` | updated |
| GET /api/v1/schedules | — | cron jobs |
| POST /api/v1/schedules | `{name, cron, prompt, targets}` | job |
| DELETE /api/v1/schedules/{id} | — | 204 |
| GET /api/v1/gateways | — | gateways + status |
| POST /api/v1/gateways/{id}/start | — | started |
| POST /api/v1/gateways/{id}/stop | — | stopped |
| GET /api/v1/logs | `?level=&limit=` | log lines |
| POST /api/v1/backup | — | backup blob |
| POST /api/v1/restore | blob | restored |

## SSE protocol (port Hermes Desktop parser exactly)
Events:
```
message, message_start, message_end, tool_start, tool_progress, tool_end,
thinking, error, done
```
Parser pseudocode (`SseClient.kt`):
```
flow {
  val channel = client.post { url(...); setBody(req); header("Accept","text/event-stream") }
  val raw = channel.bodyAsChannel()
  raw.readAvailable(buffer) in loop:
    split by "\n\n"
    for each block:
      parse "event: X" and "data: Y"
      parse JSON -> ChatEvent sealed
      emit(event)
}
```
`ChatEvent` sealed:
```
data class Message(val delta: String, val sessionId: String)
data class MessageStart(val sessionId: String, val title: String)
data class MessageEnd(val sessionId: String, val tokensIn: Int, val tokensOut: Int, val cost: Double)
data class ToolStart(val name: String, val args: JsonObject)
data class ToolProgress(val name: String, val progress: Double, val message: String)
data class ToolEnd(val name: String, val result: String?, val error: String?)
data class Thinking(val content: String)
data class Error(val message: String, val code: Int)
data class Done(val sessionId: String)
```

## Tolerant deserialization
Use `kotlinx.serialization.json.Json { ignoreUnknownKeys = true; isLenient = true }`.
Never crash on missing/null fields — default them.

## Capability detection
Like Scarf: detect Hermes version from `/health`; gate UI surfaces on `version >= x`.
Unknown endpoints → feature hidden, not crash.

## Error mapping
HTTP 401 → Auth error (re-prompt key). 404 → NotFound. 5xx → Server.
Timeout → Timeout. Network fail → Network. All mapped to `HermesException`.
