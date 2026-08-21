# Hermes Mobile API Contract (Android Native)

**Status: VERIFIED from source.** Written from the running Hermes gateway
(`api_server` adapter, port 8642) in this environment — the same contract the
Hermes desktop/web clients use. This is ground truth, not a guess.

Pinned gateway version in this environment: `hermes-agent` (check
`GET /v1/capabilities` at runtime for the live version the client should
display in Settings → Diagnostics).

---

## 1. Architecture decision (critical correction)

The earlier draft of this doc assumed an experimental WebSocket relay
(`gateway/relay/ws_transport.py`). That relay is **EXPERIMENTAL** and not the
client contract the desktop app uses.

**The Android client MUST target the `api_server` REST + SSE surface** — the
stable, documented, desktop-parity contract. Rationale:

- It is the same surface the Mac/desktop client consumes → guaranteed parity.
- It is REST + SSE (HTTP/1.1 + `text/event-stream`), which is far simpler and
  more robust to implement on Android than the relay's bespoke WS frame scheme.
- It is authenticated with a single static `API_SERVER_KEY` (no HMAC token
  rotation logic needed on the client).
- Reconnect/resume is first-class (`session_id` + `previous_response_id` +
  `X-Hermes-Session-Id` continuity headers).

> If the relay WS becomes the enforced contract later, this doc gets an ADR and
> a second transport module. Until then: **one transport, the api_server.**

---

## 2. Connection & authentication

| Item | Value | Notes |
|------|-------|-------|
| Base URL | user-configured, default `http://127.0.0.1:8642` | Gateway `api_server` port. On-device the gateway runs locally; remote use requires the user's tunnel/port-forward (never embed secrets). |
| Auth scheme | `Authorization: Bearer <API_SERVER_KEY>` | Static key from gateway `config.yaml` (`platforms.api_server.api_key`) or the gateway's printed startup token. Sent on **every** request including SSE. |
| Key storage | Android Keystore + DataStore | Per `AGENTS.md` security boundary. Never in plaintext, never in repo, never in logs. |
| TLS | optional | Localhost is http; remote is user's responsibility (their tunnel). Client must allow http for `127.0.0.1`/local addresses and warn on plain-http remote. |

The client connection profile model: `{ id, label, baseUrl, apiKey, isLocal }`.
Stored encrypted; the key is held in memory only during an active session.

---

## 3. REST endpoints (machine-readable surface)

All under `<baseUrl>`. Auth header required on all.

| Method | Path | Purpose |
|--------|------|---------|
| GET  | `/v1/models` | List hermes-agent + configured `model_routes` aliases. Client shows these in model picker. |
| GET  | `/v1/capabilities` | Machine-readable API capabilities for external UIs (feature flags, protocol version). **Client MUST read this on connect** and gate UI on it. |
| GET  | `/health` | Liveness probe for Settings → Diagnostics and the Home health tile. |
| GET  | `/health/detailed` | Rich status for cross-container dashboard probing (used by desktop). |
| GET  | `/api/sessions` | List client-visible Hermes sessions. |
| POST | `/api/sessions` | Create an empty Hermes session. |
| GET  | `/api/sessions/{id}` | Read a session. |
| PATCH| `/api/sessions/{id}` | Update a session (title, etc.). |
| DELETE| `/api/sessions/{id}` | Delete a session. |
| GET  | `/api/sessions/{id}/messages` | Read session message history (for resume / branch view). |
| POST | `/api/sessions/{id}/fork` | Branch a session using SessionDB lineage. |
| POST | `/api/sessions/{id}/chat[/stream]` | Chat with a persisted session. `stream=true` → SSE (preferred). |
| POST | `/v1/runs` | Start a run, returns `run_id` immediately (202). |
| GET  | `/v1/runs/{run_id}` | Retrieve current run status. |
| GET  | `/v1/runs/{run_id}/events` | **SSE** stream of structured lifecycle events (the primary live channel). |
| POST | `/v1/runs/{run_id}/approval` | Resolve a pending run approval (`once` / `session` / `always` / `deny`). |
| POST | `/v1/runs/{run_id}/stop` | Interrupt a running agent (hard stop). |
| GET  | `/v1/responses/{response_id}` | Retrieve a stored Responses-API response. |
| DELETE| `/v1/responses/{response_id}` | Delete a stored response. |

OpenAI-compat (for reference; the native endpoints above are preferred):
`POST /v1/chat/completions`, `POST /v1/responses`.

---

## 4. Chat request / response shape

### Request (POST `/api/sessions/{id}/chat` with `stream: true`)

```json
{
  "message": "user text",
  "model": "hermes-agent",          // or a provider model id + provider field
  "provider": "kilocode",           // optional explicit override
  "model_options": { "reasoning_effort": "high" },  // optional
  "stream": true
}
```

Session continuity headers (resume across reconnect without re-sending history):
- `X-Hermes-Session-Id: <id>`
- `X-Hermes-Session-Key: <key>` (long-term memory scoping, opt-in)

### Response: SSE frames (`text/event-stream`)

Each frame is `data: <json>\n\n`, optionally with an `event: <name>` line.

| event | `data` shape | Client action |
|-------|-------------|---------------|
| `delta` | `{ "text": "…" }` | Append incremental assistant text to the active bubble. Throttle recomposition (collect into a buffer, flush on a 16–50ms coalescing window). |
| `message` | `{ "role": "assistant"|"user", "content": "…", "id": "…" }` | A complete message (used for history sync / non-streaming fallback). |
| `tool` | `{ "tool_name": "…", "preview": "…", "args": {…}, "index": 0, "phase": "start"|"finish", "ok": true, "duration": 1.2 }` | Render/settle a tool-activity chip in the Activity timeline. |
| `thinking` | `{ "text": "…" }` | Optional reasoning block (respect reduced-motion / settings). |
| `run` (lifecycle) | `{ "run_id": "…", "status": "queued"|"running"|"awaiting_approval"|"done"|"stopped", "approval": {…} }` | Drive run state machine; surface approval controls when `awaiting_approval`. |
| `error` | `{ "code": "…", "message": "…" }` | Render error state; never auto-retry destructive actions. |

The client's streaming state machine:

```
IDLE → SEND → STREAMING(delta*)
     → AWAITING_APPROVAL (render Approve/Deny; user resolves → resume STREAMING)
     → DONE → IDLE
     → ERROR → (retry-safe: user-initiated only) → IDLE
```

---

## 5. Approval & interrupt (hard rules)

- An approval request arrives as a `run` frame with `status: "awaiting_approval"`
  and an `approval` object. Client renders explicit Approve/Deny + scope
  (`once` / `session` / `always`). Resolving calls `POST /v1/runs/{id}/approval`.
- **Reconnect MUST NOT auto-resend the last prompt or auto-approve anything.**
  Approval and interrupt controls stay bound to the original `run_id`.
- Interrupt: `POST /v1/runs/{id}/stop`. Must be user-initiated, never automatic
  after reconnect.
- Dangerous actions (per `AGENTS.md`) require explicit approval and must never
  auto-approve after reconnect.

---

## 6. Reconnect & resume semantics

- The client keeps the active `session_id` and last `response_id`/`run_id`.
- On socket drop: enter RECONNECTING (show reconnect state, keep drafts).
- On resume: re-open `GET /v1/runs/{run_id}/events` if a run is mid-flight, or
  re-POST chat for a fresh turn with the `X-Hermes-Session-Id` header so the
  gateway correlates history server-side.
- Drafts are preserved locally across reconnect (Room/DataStore) — never lost.
- Idempotency: the gateway correlates by `session_id`; the client MUST NOT
  double-submit a prompt on reconnect. Track an in-flight `requestId` per send.

---

## 7. Unknown-field tolerance

The client deserializes with a strict-but-extensible model: known fields mapped,
unknown fields preserved in a `@RawValue`/extra map and ignored for behavior.
**The client must tolerate unknown fields/events and must not invent behavior
for unknown commands** (per mobile contract).

---

## 8. What the client is NOT

Per `README.md` product boundary, the app does **not** contain CCR, a duplicate
router, a second inference gateway, a WebView desktop clone, or a separate
Android agent runtime. It is a secure client for the Hermes gateway. All
inference, routing, skills, tools, agents, memory, cron, and NOUS operations
remain authoritative server-side.

---

## 9. Evidence sources (for audit)

- `hermes-agent/gateway/platforms/api_server.py` — endpoint table, auth, SSE frame serializer (`_sse_frame`, `event:` names `delta`/`message`/`tool`/`run`).
- `hermes-agent/gateway/stream_events.py` — `StreamEvent` taxonomy (MessageChunk, MessageStop, Commentary, ToolCallChunk, ToolCallFinished, LongToolHint, GatewayNotice).
- `hermes-agent/gateway/relay/{ws_transport,auth}.py` — confirmed EXPERIMENTAL; explicitly NOT the client contract.
- Running gateway state: `~/.hermes-gw2/gateway_state.json` (pid live, `api_server` platform present though currently port-in-use).
