# DESIGN_SYSTEM.md — Hermes Android

> Dark-terminal aesthetic inspired by Hermes Desktop + commandcode.ai, with iOS-grade polish.

## Palette

### Dark (default)
| Token | Hex |
|-------|-----|
| background | #0A0A0A |
| surface | #111111 |
| surfaceVariant | #1A1A1A |
| surfaceContainerHighest | #1E1E1E |
| outline | #222222 |
| outlineVariant | #333333 |
| primary | #22C55E |
| onPrimary | #0A0A0A |
| primaryContainer | #166534 |
| onPrimaryContainer | #DCFCE7 |
| secondary | #3B82F6 |
| tertiary | #F59E0B |
| error | #EF4444 |
| onSurface | #FAFAFA |
| onSurfaceVariant | #888888 |
| textMuted (custom) | #555555 |

### Light (optional, off by default)
Invert to near-white surfaces; keep primary emerald.

### AMOLED mode
background=`#000000`, surface=`#050505`.

## Typography
- **Headings/Stats/Labels**: JetBrains Mono (bundled `res/font/jetbrains_mono_*.ttf`)
- **Body/Buttons/Inputs**: System (Inter/SF-compatible)
```
displayLarge  57sp mono Bold
displayMedium 45sp mono Bold
headlineLarge 32sp mono Medium
headlineMedium28sp mono Medium
titleLarge    22sp mono Medium
titleMedium   16sp mono Medium
bodyLarge     16sp/24sp system
bodyMedium    14sp/20sp system
labelLarge    14sp mono Medium
labelMedium   12sp mono Medium
labelSmall    11sp mono Medium
```

## Shapes
small=8dp, medium=12dp, large=16dp, extraLarge=28dp

## Spacing grid (4dp)
4, 8, 12, 16, 20, 24, 32, 40, 48

## Card style
1px `outline` border, radius 12dp, **NO** elevation/shadow. Use `surface` fill.

## Animation principles
- Page transitions: `AnimatedContent` fade + 8dp slide
- List items: `animateItemPlacement` + `Modifier.animateItem()`
- Buttons/loading: `animate*AsState`
- Chat streaming: typewriter per delta
- Loaders: `OrbLoader` (ported from Hermes Desktop) + shimmer
- Respect `LocalAccessibilityManager.kt` for reduced motion

## Component implementation rules
Every component in `ui/components/`:
1. `@Composable` with explicit `Modifier = Modifier` first param
2. `@Preview(showBackground=true)` with dark theme
3. `semantics { contentDescription = ... }` on interactive/icon-only elements
4. Min touch target 48dp
5. Documented params (kdoc)
6. No hardcoded strings — pass via params or `stringResource`

## Component list (52) — see master plan §3.4
Implemented in `ui/components/Hermes*.kt`. Each gets a `Previews` file or `@Preview` in-place.
