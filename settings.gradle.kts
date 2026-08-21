pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "HermesAndroidNative"

// ── Core (shared, no feature logic) ──────────────────────────────────────────
include(":core:common")          // result types, extensions, coroutine utils, errors
include(":core:design")          // Figma tokens, themes, MotionSpec, component primitives
include(":core:gateway")         // api_server client: REST+SSE, auth, event state machine, reconnect
include(":core:data")            // Room, DataStore, repository impls, connection profiles
include(":core:navigation")      // NavGraph contracts, deep links, Route sealing
include(":core:ui")              // shared Compose widgets (states, sheets, lists, empty/error/offline)

// ── Feature modules (each independently testable, owned by one agent) ─────────
include(":feature:home")         // gateway health, active work, recent sessions, approvals
include(":feature:chat")         // streaming conversation, composer, tools, attachments, voice
include(":feature:activity")     // agent run timeline, tools, sub-agents, approvals, failures
include(":feature:sessions")     // search, resume, branch, usage, history
include(":feature:nous")         // search, capture, review, project context, vault health
include(":feature:automations")  // bounded schedules, run status, logs, delivery state
include(":feature:settings")     // gateways, security, appearance, notifications, diagnostics

// ── App shell ────────────────────────────────────────────────────────────────
include(":app")
