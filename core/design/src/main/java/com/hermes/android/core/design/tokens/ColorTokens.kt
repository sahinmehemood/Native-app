package com.hermes.android.core.design.tokens

import androidx.compose.ui.graphics.Color

/**
 * Canonical color tokens — the single source of truth for the app palette.
 *
 * These mirror the Hermes desktop identity (deep neutral surfaces, a single
 * accent) and are intentionally decoupled from Material3's defaults so the app
 * *feels like Hermes*, not like a stock Material sample.
 *
 * When the Figma foundation is delivered, the Figma plugin's design-token JSON
 * is mapped 1:1 onto these names (see DESIGN_TOKENS_SCHEMA.md). No composable
 * should hardcode a Color literal — it must reference a token here.
 */
object HermesColorTokens {
    // ── Brand / accent ────────────────────────────────────────────────
    val Accent = Color(0xFF7C5CFF)        // primary action, links, active states
    val AccentSoft = Color(0xFF2A2350)    // tinted surface behind accent content
    val AccentOn = Color(0xFFFFFFFF)      // content on accent

    // ── Semantic ─────────────────────────────────────────────────────
    val Success = Color(0xFF3DDC84)
    val Warning = Color(0xFFFFB020)
    val Danger = Color(0xFFFF5C5C)
    val Info = Color(0xFF5AC8FA)

    // ── Status (agent run lifecycle) ─────────────────────────────────
    val StatusIdle = Color(0xFF8A8A99)
    val StatusRunning = Color(0xFF7C5CFF)
    val StatusAwaiting = Color(0xFFFFB020)
    val StatusDone = Color(0xFF3DDC84)
    val StatusStopped = Color(0xFF8A8A99)
    val StatusError = Color(0xFFFF5C5C)

    // ── Chat author roles ────────────────────────────────────────────
    val RoleUser = Color(0xFF7C5CFF)
    val RoleAssistant = Color(0xFFE6E6F0)
    val RoleTool = Color(0xFF5AC8FA)
    val RoleSystem = Color(0xFF8A8A99)

    // ── Surfaces: dark theme ─────────────────────────────────────────
    object Dark {
        val Background = Color(0xFF0B0B10)
        val Surface = Color(0xFF16161E)
        val SurfaceVariant = Color(0xFF1F1F2A)
        val SurfaceRaised = Color(0xFF242433)
        val Border = Color(0xFF2E2E3C)
        val OnSurface = Color(0xFFE6E6F0)
        val OnSurfaceMuted = Color(0xFF9A9AA8)
        val OnSurfaceSubtle = Color(0xFF6A6A78)
    }

    // ── Surfaces: light theme ────────────────────────────────────────
    object Light {
        val Background = Color(0xFFF7F7FB)
        val Surface = Color(0xFFFFFFFF)
        val SurfaceVariant = Color(0xFFF0F0F6)
        val SurfaceRaised = Color(0xFFE9E9F2)
        val Border = Color(0xFFDCDCE6)
        val OnSurface = Color(0xFF16161E)
        val OnSurfaceMuted = Color(0xFF5A5A68)
        val OnSurfaceSubtle = Color(0xFF8A8A99)
    }
}
