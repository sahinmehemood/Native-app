# AGENTS.md — Hermes Android

> **Read this file FIRST at the start of every session.** It re-orients the agent
> after context compaction. The repo is our long-term memory.

## Project
Native Android app replicating **Hermes Desktop** (Hermes One) with iOS-quality dark-terminal design.
- Package: `com.hermes.android`
- Repo: `github.com/sahinmehemood/App-agnets`
- Stack: Kotlin + Jetpack Compose, Hilt, Room, Ktor, Navigation Compose, Material 3
- Architecture: Clean (data/domain/presentation) + MVI (StateFlow)
- Design: Hermes Desktop dark terminal — `#0A0A0A` bg, `#22C55E` accent, JetBrains Mono headings

## Mandatory session start
1. `git pull --rebase`
2. Re-read in order:
   - `docs/HERMES_ANDROID_MASTER_PLAN.md`
   - `docs/ARCHITECTURE.md`
   - `docs/DESIGN_SYSTEM.md`
   - `docs/API_INTEGRATION.md`
   - `docs/DATABASE_SCHEMA.md`
   - `docs/RELEASE_PROCESS.md`
3. Check the current milestone in master plan §16. **Work ONLY on that milestone.**

## Build & verify commands
```bash
./gradlew assembleDebug          # build debug APK
./gradlew assembleRelease        # build release APK
./gradlew testDebugUnitTest      # unit tests
./gradlew ktlintCheck detekt lint # static analysis
./gradlew build                  # full build (use after changes)
```

## Commit conventions
- `feat(scope): msg`
- `fix(scope): msg`
- `chore(scope): msg`
- `docs: msg`
- `test(scope): msg`
- `refactor(scope): msg`

## Rules (NON-NEGOTIABLE)
1. **Every pushed commit must compile** and have green unit tests for the touched code.
2. **Dark theme is the default.** All UI must look correct on `#0A0A0A`.
3. **No hardcoded strings.** Put everything in `res/values/strings.xml`.
4. **No secrets in code.** API keys → `EncryptedSharedPreferences`. Never commit `local.properties` or keystore.
5. **Every screen implements MVI**: `UiState` sealed class (Loading/Empty/Error/Success), ViewModel exposes `StateFlow<UiState>` + event `SharedFlow`.
6. **Every composable has `@Preview` (dark)** and `semantics` for accessibility.
7. **Small verifiable steps.** After each logical chunk, run `./gradlew build`. Do not write 5 files then build.
8. **Tolerant JSON deserialization.** Never crash on unknown API fields.
9. **Offline-first.** Reads from Room; remote writes enqueue to `SyncEntity` when offline.
10. **This master plan is the contract.** Do not add features outside scope; put additions in a backlog note at the end of `CHANGELOG.md`.

## Project layout (key dirs)
- `app/src/main/java/com/hermes/android/`
  - `di/` Hilt modules
  - `data/{api,db,prefs,repository,source}/`
  - `domain/{model,repository,usecase}/`
  - `presentation/{navigation,ui/{theme,components,screen},viewmodel,state,widgets}/`
  - `util/`
- `docs/` — all specifications
- `.github/workflows/` — CI/CD

## Current status (update at end of each session)
See `CHANGELOG.md` "WORK LOG" section and master plan §16 for milestone gates.

## Reference apps studied
- Hermes Desktop (fathah/hermes-desktop) — feature parity target
- Hermex (uzairansaruzi/hermex) — iOS SwiftUI reference for design/UX
- Scarf/ScarfGo (awizemann/scarf) — iOS SwiftUI reference for design/UX
- Hermes Agent core (NousResearch/hermes-agent) — API/server
