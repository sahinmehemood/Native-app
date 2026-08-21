# Multi-Agent Build Playbook (Hermes Android Native)

**How this repo is built:** one orchestrator (this Hermes agent) + isolated
subagents, each owning one module. Mirrors the Claude-Code autonomous loop
(plan → approve → auto-edit → build → test → improve) and your `agent-workflow.md`
(specify → design → implement → review → verify, no direct-to-main merges).

## Roles (mapped to `.agents/roles/`)

| Role | Owns | Agent type |
|------|------|-----------|
| product-architect | scope, acceptance criteria, ADRs | orchestrator-only (no UI) |
| hermes-protocol | gateway contract + fixtures | orchestrator (verified from source) |
| android-platform | Compose arch, nav, lifecycle, cache | subagent per module |
| design-system | Figma tokens → Compose, components | subagent (runs `scripts/figma_sync.py`) |
| qa-engineer | unit/UI/screenshot/regression tests | subagent per feature |
| security-reviewer | keystore, transport, approvals, logs | orchestrator gate |

## The loop (per feature)

1. **Spec** — orchestrator writes `docs/features/<name>.md`: scope, acceptance
   criteria, owned paths, forbidden paths, verification commands.
2. **Design** — design-system agent maps Figma tokens (or default set) to the
   feature's composables; references an approved Figma frame.
3. **Implement** — a single isolated subagent builds the feature module against
   the contract + spec. It sees ONLY its module + `core:*` APIs, not the whole
   repo (context isolation = no 1M-LOC overflow).
4. **Verify** — subagent writes unit tests; CI runs build + ktlint + detekt +
   unit + emulator UI tests + gitleaks on the PR branch.
5. **Review** — orchestrator checks architecture boundary + screenshot regression.
6. **Merge** — green PR → `main` via the agent-workflow gate (no direct push).

## Why this scales to 1M LOC

- **Module isolation:** 13 Gradle modules; each agent edits one, zero cross-merge
  conflict on happy path.
- **Contract-first:** `HERMES-MOBILE-API.md` + `core:*` APIs are the only
  inter-module surface; agents never guess each other's internals.
- **CI is the build surface**, not Termux (no Android SDK on phone). All heavy
  compile/test runs free on GitHub Actions runners.
- **Quality is enforced, not hoped:** ktlint (formatting) + detekt (bugs/
  complexity) + architecture-boundary + gitleaks are hard CI gates.

## Current state

- Phase 0 contract: DONE (verified from live gateway source).
- Architecture + design tokens (default) + CI hardening: DONE.
- Figma foundation: PENDING your dedicated file (see FIGMA-DESIGN-SPEC.md).
- Feature agents: not yet spawned (start after Figma tokens land).
