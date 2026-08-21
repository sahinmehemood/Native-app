# Delivery Roadmap

## Phase 0 — Discovery gate

- Pin the Hermes version used by the second device.
- Inspect the official desktop gateway client and current gateway implementation.
- Verify transport, authentication, capabilities, sessions, streaming, tools, approvals, files, and reconnect behavior.
- Produce `HERMES-MOBILE-API.md` from evidence, not guesses.
- Create fake gateway fixtures for Android tests.
- Identify the migration boundary from `hermes-nous`.

Gate: protocol contract reviewed and approved.

## Phase 1 — Product and interaction design

- Audit Hermes Desktop visual language.
- Define phone-first navigation and tablet/foldable adaptations.
- Design chat, activity timeline, approvals, previews, sessions, NOUS, automations, settings, and diagnostics.
- Define all loading, empty, error, offline, reconnect, permission, and reduced-motion states.
- Build the Hermes Android Figma foundation and motion prototypes.

Gate: Figma prototype and state matrix approved.

## Phase 2 — Android foundation

- Establish modular Kotlin/Compose architecture.
- Add secure connection profiles, local cache, diagnostics, navigation, design tokens, and test fixtures.
- Add CI checks for build, lint, static analysis, tests, secret scanning, and architecture boundaries.

Gate: clean build and testable application shell.

## Phase 3 — First vertical slice

- Connect to a real Hermes gateway.
- Create/resume a session.
- Stream messages and tool events.
- Preserve drafts across reconnect.
- Render approval and interruption controls.
- Verify desktop and Android can coexist on one session.

Gate: polished real-device chat slice accepted against Figma and protocol fixtures.

## Phase 4 — Product parity

- Sessions and search.
- Tool activity and sub-agent timeline.
- File browser and previews.
- Voice and attachments.
- Settings and gateway management.
- Notifications and supported background completion.

Gate: Mac parity matrix complete for the agreed mobile scope.

## Phase 5 — NOUS and agent work management

- NOUS search and safe capture.
- Inbox/review flows.
- Research and provenance views.
- Task queue, scheduled jobs, approvals, run history, retry/resume, and generated artifacts.

Gate: every write is scoped, auditable, reversible where practical, and tested.

## Phase 6 — Hardening and release

- Device matrix, accessibility, performance, battery, offline, reconnect, security, and visual regression testing.
- Signed internal builds, closed testing, release notes, privacy review, and rollback plan.

Gate: release candidate approved.

