# Hermes Agent — Android

Native Android app replicating the **Hermes Desktop** (Hermes One) experience with
iOS-grade dark-terminal design. Built with Kotlin + Jetpack Compose.

> **This is a community project**, not affiliated with Nous Research or the Hermes Desktop
> maintainers. It is a client for your own Hermes Agent server (local via Termux or remote).

## Features (roadmap)

Full parity with Hermes Desktop:
- 💬 **Chat** — SSE streaming, markdown, tool progress, token usage, 22 slash commands
- 📚 **Sessions** — search (FTS5), resume, date groups
- 🤖 **Profiles/Agents** — switch isolated Hermes environments
- 🧩 **Skills** — browse, install, manage
- 🧠 **Models** — provider + local model discovery
- 📝 **Memory & Soul** — edit MEMORY.md, USER.md, SOUL.md
- 🔧 **Tools** — toggle 14 toolsets
- ⏰ **Schedules** — cron builder + delivery targets
- 🌐 **Gateway** — 16 messaging platforms
- ⚙️ **Settings** — provider, credentials, backup, logs, theme

## Connection

- **Local**: run Hermes on-device via Termux
- **Remote**: connect to your Hermes API server (URL + API key)

## Build

```bash
# Local (Termux/CI)
./gradlew assembleDebug
# Install
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

CI builds signed APKs automatically. See **Releases** for downloadable APKs.

## Development

Read `AGENTS.md` and `docs/` before contributing. The master plan lives in
`docs/HERMES_ANDROID_MASTER_PLAN.md`.

## License

MIT — see [LICENSE](LICENSE).
