# Hermes Android — Figma Design File Spec

**Goal:** a purpose-built, dedicated Figma file that is the **visual source of
truth** for the Hermes Android Native app. The app's `core:design` module is
generated from this file's variables/components (see `scripts/figma_sync.py`).

Per `README.md`: *Figma is the visual source of truth.* Per `QUALITY-GATES.md`:
every screen needs Figma frame references, phone + tablet/foldable, dark theme,
large font, reduced-motion, and all loading/empty/error/offline/reconnect states.

---

## 1. File structure (pages)

1. **Foundations**
   - Color variables (primitive + semantic)
   - Spacing / sizing variables
   - Radius / elevation variables
   - Typography styles
   - Motion tokens (duration + easing curves)
2. **Components** — every reusable piece as a variant component:
   - `Surface/Card`, `Sheet/Bottom`, `Chip`, `Button/Primary|Secondary|Ghost|Tonal`
   - `TextField/Composer`, `Avatar/Agent|User`, `IconButton`, `Toggle`, `Badge/Status`
   - `MessageBubble/User|Assistant|Tool|System`, `ToolActivityRow`, `ApprovalCard`
   - `StateEmpty`, `StateError`, `StateOffline`, `StateLoading`, `StateReconnect`
   - `NavRail` (tablet/foldable), `TopBar`, `ListItem`, `Modal/Dialog`
3. **Screens** (phone portrait + landscape; tablet/foldable two-pane)
   - Home, Chat (idle/streaming/approval/reconnect), Activity, Sessions,
     NOUS (search/capture/review), Automations, Settings (+ Diagnostics).
4. **Motion prototypes** — at least Home→Chat and Chat→Approval flows.

---

## 2. Variable collections (the import contract)

The syncer reads `GET /v1/files/:key/variables/local` and maps:

| Figma collection | Mode(s) | → Compose target |
|------------------|---------|------------------|
| `Primitive/Color` | — | base hex values (no theme) |
| `Semantic/Color` | `Dark`, `Light` | `HermesColorTokens.Dark/Light` |
| `Primitive/Spacing` | — | `HermesSpacing` |
| `Primitive/Radius` | — | shape tokens |
| `Primitive/Elevation` | — | `HermesElevation` |
| `Motion/Duration` | — | `HermesMotion.Duration*` |
| `Motion/Easing` | — | `HermesMotion.Ease*` |

Naming rule: Figma variable name → Kotlin token name (e.g. `color.accent` →
`HermesColorTokens.Accent`). The syncer emits `ColorTokens.kt` and the theme
references exactly these names — **no composable hardcodes a Color literal.**

---

## 3. Brand direction (so it doesn't look generic)

- Deep near-black surfaces (`#0B0B10` bg) with a single confident accent
  (violet `#7C5CFF`) — the Hermes identity already in the desktop app.
- Hairline borders + restrained elevation (flat, premium, not "Material sample").
- One accent, high contrast, generous spacing, 48dp touch targets.
- Status colors for agent-run lifecycle: idle/running/awaiting/done/stopped/error.

---

## 4. Handoff

When the file is ready, send the **file link/key**. The syncer
(`scripts/figma_sync.py`, run with `FIGMA_TOKEN`) fetches variables and writes
`core/design/.../tokens/*.kt`. CI then proves the app compiles against *your*
real tokens. No pixel-guessing.
