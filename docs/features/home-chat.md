# Feature: Home + Chat (Phase 3 vertical slice)

**Branch:** `feature/home-chat` (off `foundation/phase0-contract`). Never merge to `main`.

## Scope
Build two feature modules that prove the end-to-end client works against the
real Hermes `api_server` contract (docs/HERMES-MOBILE-API.md).

### feature:home
- Surfaces: gateway health tile (GET /health), active work summary, recent
  sessions list (GET /api/sessions), pending approvals badge.
- All states required: loading, empty, error, offline, reconnect.
- Navigates to Chat on session select.

### feature:chat
- Composer (48dp touch target), streaming message list, tool-activity chips.
- Connects via `core:gateway` SSE (POST /api/sessions/{id}/chat stream=true).
- Renders `delta` (coalesced, no excessive recomposition), `tool`, `run` frames.
- Approval card when `run.status == awaiting_approval` -> POST /v1/runs/{id}/approval.
- Hard rules (from contract): reconnect NEVER auto-resends prompt or auto-approves;
  drafts preserved locally (DataStore) across reconnect.

## Owned paths
- `feature/home/**`, `feature/chat/**` only.
- May depend on: `:core:design`, `:core:gateway`, `:core:data`, `:core:ui`, `:core:navigation`.
- MUST NOT touch `core:gateway` internals or other feature modules.

## Acceptance criteria
- Compiles against existing `core:*` APIs.
- Unit tests for: chat SSE frame parsing -> UI state, reconnect idempotency
  (no double-submit), approval resolution.
- Uses `HermesTheme` + tokens (no raw Color literals). All loading/empty/error/
  offline/reconnect states present.
- ktlint + detekt clean (CI gates).

## Verification
- `./gradlew :feature:home:testDebugUnitTest :feature:chat:testDebugUnitTest`
  (runs in CI; Termux cannot build). Subagent must state it could not run Gradle
  locally and rely on CI.
