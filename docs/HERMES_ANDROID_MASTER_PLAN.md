# HERMES ANDROID — MASTER SPECIFICATION v1.0

*Full Hermes Desktop parity · Native Android · Kotlin + Jetpack Compose · Dark-terminal iOS-grade design*

> **This document is the single source of truth.** Re-read it at the start of every session
> (along with `AGENTS.md` and `docs/ARCHITECTURE.md`). The repo itself is our long-term
> memory; this plan survives model compaction because it lives in Git.

---

## 0. EXECUTIVE SUMMARY

| Item | Decision |
|------|----------|
| App name (display) | Hermes Agent |
| Package | `com.hermes.android` |
| Target repo | `github.com/sahinmehemood/App-agnets` |
| Language | Kotlin 100%, Jetpack Compose UI |
| Architecture | Clean Architecture + MVI (single-activity, StateFlow) |
| Min SDK / Target / Compile | 26 / 34 / 34 |
| Design | Hermes Desktop dark-terminal: `#0A0A0A` bg, `#22C55E` accent, JetBrains Mono headings |
| Connection | BOTH: local Termux Hermes + remote API server (URL + key) |
| Estimate | ~53,000 lines, 16 phases, 22 weeks |
| APK delivery | GitHub Actions → GitHub Releases (auto-upload on tag) |
| Persistence | Repo files = memory; `AGENTS.md` + this plan re-read every session |

---

## 1. TECH STACK & DEPENDENCIES (version catalog `gradle/libs.versions.toml`)

| Dependency | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.0.x | Language |
| Compose BOM | 2024.09.x | UI toolkit |
| Material 3 | via BOM | Design system |
| Navigation Compose | 2.8.x | Type-safe navigation |
| Hilt | 2.52 | DI |
| Room | 2.6.1 | Local DB + FTS5 |
| Ktor Client | 2.3.x | HTTP/SSE/WS |
| kotlinx.serialization | 1.7.x | JSON |
| OkHttp/Okio | 4.12 | HTTP engine + streaming |
| Coil | 2.7.0 | Image loading |
| DataStore / EncryptedSharedPreferences | 1.1.x | Secure prefs (Keystore) |
| WorkManager | 2.9.x | Background sync |
| Glance | 1.1.x | Home-screen widget |
| Biometric | 1.1.0 | Biometric lock |
| markdown-android | 0.5.0 | Chat markdown |
| JUnit5 | 5.10.x | Unit tests |
| MockK | 1.13.x | Mocking |
| Turbine | 1.1.0 | Flow testing |
| Compose Test | via BOM | UI tests |
| Paparazzi | 1.3.x | Screenshot tests |
| LeakCanary | 2.14 | Leak detection |
| ktlint | 12.x | Linting |
| detekt | 1.23.x | Static analysis |

**Pin exact versions in `libs.versions.toml`.** Do not use `+` ranges in production.

---

## 2. PROJECT STRUCTURE (EVERY FILE THAT MUST EXIST)

```
App-agnets/
├── AGENTS.md                        # Session ritual + rules (re-read every session)
├── README.md                        # Install, screenshots, badges
├── LICENSE                          # MIT
├── CONTRIBUTING.md
├── .gitignore
├── .editorconfig
├── gradle.properties                # JVM args, caching, parallel
├── settings.gradle.kts              # pluginManagement + dependencyResolutionManagement + include(":app")
├── build.gradle.kts                 # root: plugins apply false
├── gradle/libs.versions.toml        # ALL versions in one place
├── gradle/wrapper/gradle-wrapper.properties   # Gradle 8.9
├── keystore.properties.example      # (gitignored real one) signing config
├── local.properties.example         # (gitignored real one) sdk.dir
├── .github/
│   ├── workflows/
│   │   ├── ci.yml                   # PR: lint+test+build
│   │   ├── release.yml              # tag: sign+release APK/AAB
│   │   └── nightly.yml              # schedule: debug APK artifact
│   ├── dependabot.yml
│   └── ISSUE_TEMPLATE/{bug,feature}.yml
├── docs/
│   ├── HERMES_ANDROID_MASTER_PLAN.md   # ← THIS DOC
│   ├── ARCHITECTURE.md
│   ├── API_INTEGRATION.md
│   ├── DATABASE_SCHEMA.md
│   ├── DESIGN_SYSTEM.md
│   └── RELEASE_PROCESS.md
└── app/
    ├── build.gradle.kts             # application plugin + all deps + signing + proguard
    ├── proguard-rules.pro
    └── src/
        ├── main/AndroidManifest.xml
        ├── main/java/com/hermes/android/
        │   ├── HermesApplication.kt
        │   ├── MainActivity.kt
        │   ├── di/
        │   │   ├── AppModule.kt            # application-level singletons
        │   │   ├── NetworkModule.kt        # Ktor client, SSE, interceptors
        │   │   ├── DatabaseModule.kt       # Room + SQLCipher
        │   │   ├── RepositoryModule.kt     # repo bindings
        │   │   └── UseCaseModule.kt        # use case bindings
        │   ├── data/
        │   │   ├── api/
        │   │   │   ├── HermesApiService.kt      # HTTP endpoints
        │   │   │   ├── SseClient.kt             # SSE streaming parser
        │   │   │   ├── WsClient.kt              # WebSocket client (optional)
        │   │   │   └── dto/                     # Request/response DTOs
        │   │   ├── db/
        │   │   │   ├── HermesDatabase.kt
        │   │   │   ├── entity/                  # 12 entity files
        │   │   │   ├── dao/                     # 8 DAO files
        │   │   │   ├── converter/               # type converters
        │   │   │   └── fts/                     # FTS5 virtual tables
        │   │   ├── prefs/
        │   │   │   ├── SecurePrefs.kt
        │   │   │   └── AppPrefs.kt
        │   │   ├── repository/                  # 15 repository impls
        │   │   └── source/                      # local/remote data sources
        │   ├── domain/
        │   │   ├── model/                       # domain models (15+)
        │   │   ├── repository/                  # repository interfaces (15)
        │   │   └── usecase/                     # use cases (~30)
        │   ├── presentation/
        │   │   ├── navigation/
        │   │   │   ├── Routes.kt                # sealed routes
        │   │   │   └── AppNavHost.kt
        │   │   ├── ui/theme/
        │   │   │   ├── Color.kt                 # palette
        │   │   │   ├── Theme.kt                 # dark/light + dynamic
        │   │   │   ├── Typography.kt            # JetBrains Mono + system
        │   │   │   ├── Shapes.kt                # radii
        │   │   │   └── Spacing.kt               # 4dp grid
        │   │   ├── ui/components/               # 52 components (§3.4)
        │   │   ├── screen/                      # 16 screens (§5)
        │   │   ├── viewmodel/                   # 16 ViewModels (MVI)
        │   │   ├── state/                       # UiState sealed classes
        │   │   └── widgets/                     # Glance widget
        │   └── util/                            # extensions, formatters, constants
        ├── main/res/
        │   ├── values/{strings,colors,themes}.xml
        │   ├── values-night/themes.xml
        │   ├── drawable/                        # vector icons
        │   ├── font/                            # jetbrains_mono_*.ttf, inter_*.ttf
        │   ├── xml/                             # backup_rules, network_security_config
        │   ├── mipmap-anydpi-v26/               # adaptive icons
        │   └── anim/                            # splash animation
        ├── test/                                # unit tests
        └── androidTest/                         # instrumented tests
```

---

## 3. DESIGN SYSTEM SPECIFICATION (EXACT VALUES)

See `docs/DESIGN_SYSTEM.md` for the full component reference. Summary:

**Colors** — app defaults to dark. `background=#0A0A0A`, `surface=#111111`,
`surfaceVariant=#1A1A1A`, `outline=#222222`, `outlineVariant=#333333`,
`primary=#22C55E`, `onPrimary=#0A0A0A`, `primaryContainer=#166534`,
`onPrimaryContainer=#DCFCE7`, `secondary=#3B82F6`, `tertiary=#F59E0B`,
`error=#EF4444`, `onSurface=#FAFAFA`, `onSurfaceVariant=#888888`,
`textMuted=#555555`. Light theme + AMOLED mode (pure #000) optional.

**Typography** — Headings/Stats/Labels: JetBrains Mono (bundled `.ttf`).
Body/Buttons/Inputs: system font (Inter/SF-compatible).
`displayLarge=57sp`, `headlineLarge=32sp`, `titleLarge=22sp`,
`bodyLarge=16sp/24sp`, `labelLarge=14sp` (mono).

**Shapes** — small=8dp, medium=12dp (default cards), large=16dp (sheets),
extraLarge=28dp (dialogs).

**Spacing grid** — 4, 8, 12, 16, 20, 24, 32, 40, 48.

**Cards** — 1px `outline` border, radius 12dp, NO elevation/shadow.

### 3.4 Component Library (52 composables — all in `ui/components/`)
| # | Component | File | Key params |
|---|-----------|------|------------|
| 1 | HermesButton | HermesButton.kt | variant: Primary/Secondary/Tertiary/Destructive, size, loading, enabled |
| 2 | HermesIconButton | HermesIconButton.kt | icon, onClick, contentDescription, tint |
| 3 | HermesCard | HermesCard.kt | onClick?, enabled, selected, padding, border |
| 4 | HermesTextField | HermesTextField.kt | value, onValueChange, label, supportingText, isError, leading/trailingIcon, singleLine |
| 5 | HermesSearchBar | HermesSearchBar.kt | query, onQueryChange, placeholder, trailing clear |
| 6 | HermesChip | HermesChip.kt | selected, onClick, leadingIcon, label |
| 7 | HermesSwitch | HermesSwitch.kt | checked, onCheckedChange, label, description |
| 8 | HermesCheckbox | HermesCheckbox.kt | checked, onCheckedChange, label |
| 9 | HermesRadioRow | HermesRadioRow.kt | options, selected, onSelect |
| 10 | HermesSegmentedButton | HermesSegmentedButton.kt | options, selected, onSelect |
| 11 | HermesSlider | HermesSlider.kt | value, onValueChange, valueRange, steps, label |
| 12 | HermesTabRow | HermesTabRow.kt | tabs, selectedIndex, onSelect, scrollable |
| 13 | HermesModalBottomSheet | HermesModalBottomSheet.kt | title, content, onDismiss, height |
| 14 | HermesBottomSheetScaffold | HermesBottomSheetScaffold.kt | sheetContent, mainContent |
| 15 | HermesDialog | HermesDialog.kt | title, text, confirm, dismiss, destructive |
| 16 | HermesInputDialog | HermesInputDialog.kt | title, initial, onConfirm, onDismiss |
| 17 | HermesAlertDialog | HermesAlertDialog.kt | severity, title, message, action |
| 18 | HermesNavDrawer | HermesNavDrawer.kt | drawerContent, mainContent |
| 19 | HermesListItem | HermesListItem.kt | title, subtitle, leading, trailing, onClick |
| 20 | HermesListHeader | HermesListHeader.kt | text, count, action |
| 21 | HermesEmptyState | HermesEmptyState.kt | icon, title, message, actionText, onAction |
| 22 | HermesErrorState | HermesErrorState.kt | message, retryAction, error |
| 23 | HermesLoadingState | HermesLoadingState.kt | label, boxed |
| 24 | HermesSkeleton | HermesSkeleton.kt | shape, size, shimmer |
| 25 | OrbLoader | OrbLoader.kt | size, color, animation — PORTED from Hermes Desktop |
| 26 | AnimatedCounter | AnimatedCounter.kt | value, format, duration |
| 27 | ShimmerBox | ShimmerBox.kt | cornerRadius |
| 28 | HermesSnackbar | HermesSnackbar.kt | message, action, severity |
| 29 | HermesProgressBar | HermesProgressBar.kt | progress, determinate, label |
| 30 | HermesBadge | HermesBadge.kt | text, color, size |
| 31 | HermesAvatar | HermesAvatar.kt | name, size, gradient |
| 32 | HermesAvatarGroup | HermesAvatarGroup.kt | avatars, max |
| 33 | HermesTooltip | HermesTooltip.kt | text, child |
| 34 | HermesDropdown | HermesDropdown.kt | items, selected, onSelect, label |
| 35 | HermesDropdownExposed | HermesDropdownExposed.kt | menu items variant |
| 36 | HermesSectionHeader | HermesSectionHeader.kt | index "//01", title, subtitle |
| 37 | HermesCodeBlock | HermesCodeBlock.kt | code, language, copyable |
| 38 | HermesMarkdown | HermesMarkdown.kt | content, isUser, onLinkClick |
| 39 | HermesToolProgress | HermesToolProgress.kt | toolName, status, progress |
| 40 | HermesToolCall | HermesToolCall.kt | name, args (collapsible) |
| 41 | HermesMessageBubble | HermesMessageBubble.kt | message, isUser, onAction |
| 42 | HermesChatInput | HermesChatInput.kt | value, onValueChange, onSend, attachments, slashActive |
| 43 | HermesSlashMenu | HermesSlashMenu.kt | commands, onSelect |
| 44 | HermesFAB | HermesFAB.kt | icon, onClick, extended |
| 45 | HermesSwitchRow | HermesSwitchRow.kt | title, subtitle, checked, onChecked |
| 46 | HermesSettingRow | HermesSettingRow.kt | icon, title, subtitle, value, onClick, chevron |
| 47 | HermesInfoBanner | HermesInfoBanner.kt | message, severity, action |
| 48 | HermesStatCard | HermesStatCard.kt | label, value, icon, trend |
| 49 | HermesChronoPicker | HermesChronoPicker.kt | cron parts, onCronChange |
| 50 | HermesQRCode | HermesQRCode.kt | content, size |
| 51 | HermesColorPicker | HermesColorPicker.kt | colors, selected, onSelect |
| 52 | HermesProfilePicker | HermesProfilePicker.kt | profiles, selected, onSelect |

**All components must have**: `@Composable`, `@Preview` (dark), correct `Modifier` params,
`semantics` for accessibility, documented params.

---

## 4. DATA LAYER SPECIFICATION

See `docs/DATABASE_SCHEMA.md` and `docs/API_INTEGRATION.md` for full detail.

### 4.1 Room Schema — 12 entities
| Entity | Key fields | Notes |
|--------|-----------|-------|
| `SessionEntity` | id (PK), title, createdAt, updatedAt, profileId, provider, model, pinned, archived, messageCount | FTS5 index on title |
| `MessageEntity` | id (PK), sessionId (FK), role, content, createdAt, model, tokensIn, tokensOut, toolCalls (JSON), status | Index on sessionId |
| `ProfileEntity` | id (PK), name, avatar, createdAt, configPath, isActive, serverMode | isolated config |
| `SkillEntity` | id (PK), name, description, version, category, installed, source, author | FTS5 |
| `ModelEntity` | id (PK), name, provider, modelId, params (JSON), favorite, lastUsed | |
| `MemoryEntity` | id (PK), type (MEMORY/USER), content, updatedAt, size | |
| `PersonaEntity` | id (PK), profileId, content (SOUL.md), updatedAt | |
| `ScheduleEntity` | id (PK), name, cron, prompt, targets (JSON), enabled, lastRunAt, nextRunAt | |
| `ScheduleRunEntity` | id (PK), scheduleId, startedAt, finishedAt, status, output | |
| `GatewayEntity` | id (PK), platform, name, config (JSON), status, lastError | |
| `ToolEntity` | id (PK), name, toolset, description, enabled | 14 toolsets |
| `SyncEntity` | id, entityType, entityId, operation, dirty (bool), timestamp | offline queue |

**DAOs (8):** SessionDao, MessageDao, ProfileDao, SkillDao, ModelDao, MemoryDao,
ScheduleDao, GatewayDao (+ ToolDao, SyncDao). FTS5 virtual tables for
Session+Message, Skill, Memory.

### 4.2 Ktor API — exact contracts
Base URL: local `http://127.0.0.1:8642` or remote configurable. Auth: `Authorization: Bearer <key>`.

| Method+Path | Request | Response |
|-------------|---------|----------|
| GET /health | — | `{status:"ok",version:"x.y.z"}` |
| GET /api/v1/config | — | full config YAML/JSON |
| POST /api/v1/config | partial config | updated config |
| GET /api/v1/providers | — | list of providers+models |
| POST /api/v1/chat | `{session_id?, message, model?, provider?, profile?, stream:true}` | SSE stream (§4.3) |
| GET /api/v1/sessions | `?query=&page=&limit=` | `{sessions:[...], total}` |
| GET /api/v1/sessions/{id} | — | full session + messages |
| DELETE /api/v1/sessions/{id} | — | 204 |
| GET /api/v1/profiles | — | profiles list |
| POST /api/v1/profiles | `{name, config}` | created profile |
| DELETE /api/v1/profiles/{id} | — | 204 |
| GET /api/v1/skills | — | skills list |
| POST /api/v1/skills/install | `{name, source}` | installed skill |
| GET /api/v1/models | — | models list |
| POST /api/v1/models | `{name, provider, model_id, params}` | saved model |
| GET /api/v1/memory | — | memory entries |
| PUT /api/v1/memory | `{entries}` | updated |
| GET /api/v1/schedules | — | cron jobs |
| POST /api/v1/schedules | `{name, cron, prompt, targets}` | created |
| DELETE /api/v1/schedules/{id} | — | 204 |
| GET /api/v1/gateways | — | gateways + status |
| POST /api/v1/gateways/{id}/start | — | started |
| POST /api/v1/gateways/{id}/stop | — | stopped |
| GET /api/v1/logs | `?level=&limit=` | log lines |
| POST /api/v1/backup | — | backup blob |
| POST /api/v1/restore | blob | restored |

### 4.3 SSE Stream events (port Hermes Desktop parser exactly)
```
event: message      →  data: {"delta":"text","content":"...","session_id":"..."}
event: message_start → data: {"session_id":"...","title":"..."}
event: message_end   → data: {"session_id":"...","tokens_in":N,"tokens_out":N,"cost":N}
event: tool_start    → data: {"name":"web_search","args":{...}}
event: tool_progress → data: {"name":"...","progress":0.5,"message":"..."}
event: tool_end      → data: {"name":"...","result":"...","error":null}
event: thinking      → data: {"content":"..."}
event: error         → data: {"message":"...","code":N}
event: done          → data: {"session_id":"..."}
```
`SseClient.kt`: Ktor `ByteReadChannel` → split by `\n\n` → parse `event:`+`data:` lines →
emit `Flow<ChatEvent>` (sealed class).

### 4.4 Repository interfaces (15) + use cases (~30)
Repos: Chat, Session, Profile, Skill, Model, Memory, Persona, Schedule, Gateway, Tool,
Config, Backup, Log, Sync, Settings.
UseCases per domain: e.g., Chat: `SendMessage`, `StreamResponse`, `StopStream`,
`RetryMessage`, `UndoMessage`. Pattern:
`class XUseCase @Inject constructor(private val repo: XRepository)`.

---

## 5. SCREENS SPECIFICATION (16 screens)

| # | Screen | File | Key states | Key components | Nav route |
|---|--------|------|-----------|----------------|-----------|
| 1 | Splash | SplashScreen.kt | Loading→Setup/Main | OrbLoader, animated logo | splash |
| 2 | Setup Wizard | SetupScreen.kt | Welcome→Mode→Provider→Profile→Done | Steps, HermesCard, HermesButton | setup |
| 3 | Main Shell | MainScreen.kt | — | BottomNav(5) + NavHost + Drawer | main |
| 4 | Chat | ChatScreen.kt | Loading/Empty/Streaming/Error | MessageBubble, ChatInput, SlashMenu, ToolProgress, TokenFooter | chat |
| 5 | Sessions | SessionsScreen.kt | Loading/Empty/List/Search | ListItem, ListHeader, SearchBar, SwipeActions, FAB | sessions |
| 6 | Session Detail | SessionDetailScreen.kt | Loading/Loaded | Message list, actions | session/{id} |
| 7 | Profiles/Agents | ProfilesScreen.kt | Loading/Empty/List | Avatar, ListItem, FAB | profiles |
| 8 | Skills | SkillsScreen.kt | Loading/Empty/List/Search | SearchBar, ListItem, Badge, install button | skills |
| 9 | Skill Detail | SkillDetailScreen.kt | Loading/Loaded | CodeBlock, HermesButton(Install) | skill/{id} |
| 10 | Models | ModelsScreen.kt | Loading/Empty/List | ListItem, AddDialog, favorite star | models |
| 11 | Memory | MemoryScreen.kt | Loading/Empty/List/Edit | tabs(MEMORY/USER), editor, save | memory |
| 12 | Persona (Soul) | PersonaScreen.kt | Loading/Edit/Preview | editor toggle, preview markdown | persona |
| 13 | Tools | ToolsScreen.kt | Loading/List | SwitchRow per toolset, InfoBanner | tools |
| 14 | Schedules | SchedulesScreen.kt | Loading/Empty/List/Create | ChronoPicker, ListItem, run history sheet | schedules |
| 15 | Gateway | GatewayScreen.kt | Loading/Empty/List | Platform cards, QR pairing, status chip | gateway |
| 16 | Settings | SettingsScreen.kt | Loading/List | SettingRow groups (10 sections), nested screens | settings |

**Settings sub-screens (7):** Provider, Credentials, Backup&Restore, Log Viewer, Network,
Appearance (theme/language), About.
**Office (Claw3d):** optional WebView screen route `office` — low priority, Phase 2.

### 5.1 Every screen MVI pattern
```kotlin
sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Loaded(
        val session: Session, val messages: List<Message>,
        val streaming: Boolean, val tokens: TokenUsage
    ) : ChatUiState
    data class Error(val message: String) : ChatUiState
}
// ViewModel: StateFlow<ChatUiState> + events -> reduce() -> new state
```

---

## 6. NAVIGATION SPEC

- Routes sealed class `Routes` (type-safe): Splash, Setup, Main, Chat, Sessions,
  SessionDetail(id), Profiles, Skills, SkillDetail(id), Models, Memory, Persona, Tools,
  Schedules, Gateway, Settings, SettingsDetail(section)
- Single `NavHost`, `MainActivity` is single-activity
- BottomNav (5): Chat, Sessions, Agents(Profiles), Skills, More(Settings drawer)
- Tablet: `NavigationRail` instead of bottom bar
- Deep links: `hermes://chat/{id}`, `hermes://sessions`
- Predictive back via `OnBackPressedCallback` + `BackHandler`
- Transitions: `AnimatedContent` fade+slide per route

---

## 7. ERROR HANDLING PATTERN

```
class HermesException(message, cause, code) : Exception
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val e: HermesException) : Result<T>
}
Error categories: Network, Auth, NotFound, Validation, Server, Timeout, Unknown
Every ViewModel exposes events as SharedFlow; screens show ErrorState + retry.
All suspend calls wrapped in runCatching -> Result.
```

---

## 8. SECURITY MODEL

| Concern | Solution |
|---------|----------|
| API keys | EncryptedSharedPreferences (Keystore-backed MasterKey) |
| Local Hermes | Connect via 127.0.0.1 loopback only; adb port-forward note in docs |
| Network | HTTPS enforced for remote; certificate pinning via OkHttp (optional build-config) |
| Cleartext | blocked except localhost (network_security_config.xml) |
| Biometric lock | BiometricPrompt → gate MainActivity on launch (setting, default OFF) |
| Backup file | AES-256-GCM, PBKDF2 100k iters from user password |
| DB | SQLCipher optional (build flag `useSqlCipher`) |

---

## 9. OFFLINE-FIRST + SYNC

- All reads from Room; remote writes enqueue to `SyncEntity` when offline
- WorkManager `PeriodicSyncWorker` (15min) + `OneTimeSyncWorker` on reconnect (ConnectivityManager)
- Conflict: server-wins for config/memory, client-wins for messages/session title
- Chat requires connection (streaming); queue unsent user messages for retry

---

## 10. TESTING STRATEGY

| Type | Tool | Coverage | Gate |
|------|------|----------|------|
| Static analysis | ktlint + detekt + Android Lint | — | CI fail on issues |
| Unit (VM/repo/usecase) | JUnit5, MockK, Turbine | ≥80% | CI |
| DAO/DB | Room in-memory + AndroidX Test | all DAOs | CI |
| API/SSE | Ktor MockEngine + contract fixtures | parser edge cases | CI |
| UI | Compose Testing + Paparazzi | critical paths | nightly + pre-release |
| E2E | manual test matrix (3 devices) | before release | release gate |

Test file layout mirrors source: `test/java/com/hermes/android/...`.

---

## 11. CI/CD PIPELINE SPEC

**ci.yml** (on PR to main):
```
jobs:
  lint:   ktlint, detekt, lint
  unit:   ./gradlew testDebugUnitTest
  build:  ./gradlew assembleDebug
```
**release.yml** (on tag `v*`):
```
jobs:
  build:
    assembleRelease + bundleRelease
    sign (keystore from secrets: SIGNING_KEYSTORE_BASE64, KEY_PASSWORD, etc.)
    create GitHub Release with changelog
    upload app-debug.apk (universal) + app-release.aab + universal-release.apk
```
**nightly.yml** (cron 00:00): assembleDebug, upload artifact (retention 7d).
**dependabot.yml**: weekly updates for Gradle + GitHub Actions.

**Keystore:** generate once (`keytool`), base64 into GitHub secret.
`keystore.properties.example` documents format. NEVER commit real keystore.

---

## 12. RELEASE PROCESS

1. Bump version in `app/build.gradle.kts` (versionCode++, versionName semver)
2. Update `CHANGELOG.md`
3. Tag `vX.Y.Z` → push → Actions builds + releases
4. APK available in GitHub Releases → user downloads/installs
5. In-app update check: `GET https://api.github.com/repos/sahinmehemood/App-agnets/releases/latest`
   → compare → `PackageInstaller`

---

## 13. PERFORMANCE BUDGET

- Cold start < 2.5s on mid-range device
- Frame time < 16ms (no jank); no overdraw in list screens
- APK size < 25MB (debug), < 12MB (release, baseline profiles + R8)
- Chat scroll: 500+ messages smooth (LazyColumn + key)
- Memory: Baseline Profile generation in CI (Macrobenchmark)
- Battery: no polling loops; WorkManager batching; SSE auto-disconnect on background

---

## 14. ACCESSIBILITY

- TalkBack contentDescriptions on ALL icons/buttons
- Min touch target 48dp
- Dynamic font scaling respected (sp units everywhere)
- Color contrast ≥4.5:1 (verified)
- Focus order logical; `semantics` on custom components
- Motion reduced: respect `Settings.System.ANIMATOR_DURATION_SCALE` / Compose `LocalAccessibilityManager`

---

## 15. LOCALIZATION

- All strings in `values/strings.xml` (en default)
- Per-app language setting via `LocaleManager` (Phase 2)
- RTL layout support (default Material)
- Community translations later: `values-zh`, `values-ja`, etc.

---

## 16. 22-WEEK SCHEDULE (with gates)

```
WK1-2  FOUNDATION: repo, gradle, design system (52 components), DI, secure prefs, CI/CD
       ▸ GATE: debug APK builds via Actions; design system compiles; tests green
WK3-4  DATA LAYER: Room (12 entities, FTS5), Ktor+SSE, 15 repos, 30 usecases, offline sync
       ▸ GATE: DAO tests pass; SSE parser contract tests pass
WK5    SETUP WIZARD: splash, local/remote, provider select, first profile
       ▸ GATE: E2E setup works local+remote
WK6    NAVIGATION SHELL: bottom nav, rail, drawer, deep links
       ▸ GATE: all routes navigable
WK7-9  CHAT CORE: streaming, markdown, slash commands, tool progress, tokens, attachments
       ▸ GATE: chat works end-to-end; streaming smooth; this is the "wow" milestone
WK10   SESSIONS: list, search (FTS5), swipe actions, offline cache
WK11   PROFILES: CRUD, switching, isolated config
WK12   SKILLS: browser, install/update/uninstall, hub
WK13   MODELS: provider models, local discovery, favorites
WK14   MEMORY + PERSONA: editors, providers, live preview
WK15   TOOLS + SCHEDULES: toolset grid, cron builder, run history
WK16   GATEWAY: 16 platforms, config forms, QR pairing
WK17   OFFICE + SETTINGS: claw3d webview, 7 settings subsections
WK18-19 POLISH: animations, performance, accessibility, tablet, RTL
WK20   RELEASE: signed AAB/APK, changelog, release tag, update check
WK21-22 BUG-FIX BUFFER + beta feedback loop
```

---

## 17. DEFINITION OF DONE (every feature)

```
☐ Code compiles (gradlew build green)
☐ ktlint + detekt + lint clean
☐ Unit tests ≥80% on touched code, all green
☐ UI state handles Loading/Empty/Error/Success
☐ Accessibility: contentDescriptions, contrast, 48dp
☐ Dark theme correct (app defaults dark)
☐ Previews added (dark)
☐ No hardcoded strings (all in strings.xml)
☐ No secrets in code
☐ Offline behavior defined (cache/queue)
☐ Performance checked (no jank in list)
☐ Committed with descriptive message + pushed
```

---

## 18. RISK MITIGATION

| Risk | Impact | Mitigation |
|------|--------|------------|
| Session compaction loses context | High | AGENTS.md + master plan + architecture docs re-read each session; repo = memory |
| Free model quality variance | High | Small verifiable steps; build after every chunk; contract tests pin behavior; user reviews at gates |
| Hermes API changes | Med | Versioned client; capability detection (like Scarf); tolerant deserialization |
| Termux environment variance | Med | Document tested path; fallback to remote-only mode |
| Large message DB | Med | Paging, FTS5, background compaction |
| CI signing complexity | Med | Document keystore setup once; test in nightly first |
| Scope creep | High | This spec = contract; feature adds go to backlog, not mid-build |

---

## 19. SESSION HANDOFF PROTOCOL (see `AGENTS.md`)

Every session: `git pull`, re-read `AGENTS.md` + docs, work only on the current milestone,
build+test after each chunk, commit+push when green.

---

## 20. EXECUTION ORDER (current phase)

```
1. Write AGENTS.md + docs/ (this spec split into the 6 docs)
2. Verify tooling: java, gradle, sdkmanager, android sdk, git, gh
3. Clone github.com/sahinmehemood/App-agnets
4. Push: AGENTS.md, docs/, README, LICENSE, .gitignore, .editorconfig
5. Scaffold gradle project + libs.versions.toml + build files
6. Implement design system (Color/Theme/Typography/Shapes/Spacing + 52 components)
7. Hilt DI + Ktor + Room + Secure Prefs skeleton
8. CI workflows (ci/release/nightly)
9. Build debug APK → commit → push → confirm Actions produces APK
```
