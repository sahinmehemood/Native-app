# Contributing

Welcome! This is a large, structured project. Read first:

1. `AGENTS.md` — session ritual, build commands, rules
2. `docs/HERMES_ANDROID_MASTER_PLAN.md` — the contract
3. `docs/ARCHITECTURE.md`, `docs/DESIGN_SYSTEM.md`, `docs/API_INTEGRATION.md`,
   `docs/DATABASE_SCHEMA.md`, `docs/RELEASE_PROCESS.md`

## Rules
- Every pushed commit must compile + green unit tests
- Dark theme is default
- No hardcoded strings (use `res/values/strings.xml`)
- No secrets in code
- Every screen: MVI + `@Preview` + accessibility
- Small verifiable steps, build after each chunk

## Commit style
`feat(scope): msg` · `fix(scope): msg` · `chore(scope): msg` · `docs: msg`
