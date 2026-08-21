#!/usr/bin/env python3
"""
gen_tokens.py — design-system source -> Hermes Android Compose tokens.

Reads design-system/hermes-tokens.json and emits the real Kotlin token files
into core/design. This is the authoritative generator: the JSON is the single
source of truth, the Compose code is generated (never hand-maintained), so the
app can never drift from the design intent. Mirrors how professional design
systems sync Figma -> code.

Usage: python3 scripts/gen_tokens.py
"""
import json, os

ROOT = os.path.join(os.path.dirname(__file__), "..")
SRC = os.path.join(ROOT, "design-system", "hermes-tokens.json")
TOK = os.path.join(ROOT, "core", "design", "src", "main", "java",
                   "com", "hermes", "android", "core", "design", "tokens")


def load():
    with open(SRC, encoding="utf-8") as f:
        return json.load(f)


def primitive_hex(d, name):
    return d["primitive"]["color"][name]


def gen_colors(d):
    prim = d["primitive"]["color"]
    out = ["package com.hermes.android.core.design.tokens", "",
           "import androidx.compose.ui.graphics.Color", "", "object HermesColorTokens {"]
    for mode in ("dark", "light"):
        out.append(f"    object {mode.capitalize()} {{")
        for sem, prim_name in d["semantic"]["color"][mode].items():
            hexv = prim.get(prim_name, "#000000")
            # Kotlin Color takes 0xAARRGGBB. Strip '#', ensure 6-digit, prefix 0xFF.
            rgb = hexv.lstrip("#").upper()
            if len(rgb) == 6:
                kotlin_color = f"0xFF{rgb}"
            elif len(rgb) == 8:
                kotlin_color = f"0x{rgb}"
            else:
                kotlin_color = "0xFF000000"
            out.append(f'        val {sem} = Color({kotlin_color})')
        out.append("    }")
    out.append("}")
    return "\n".join(out) + "\n"


def gen_shape(d):
    sp = d["primitive"]["spacing"]
    rad = d["primitive"]["radius"]
    el = d["primitive"]["elevation"]
    lines = ["package com.hermes.android.core.design.tokens", "",
             "import androidx.compose.ui.unit.dp", "import androidx.compose.ui.unit.Dp", "",
             "object HermesSpacing {",
             f'    val None = {sp["0"]}.dp', f'    val Xs = {sp["xs"]}.dp',
             f'    val Sm = {sp["sm"]}.dp', f'    val Md = {sp["md"]}.dp',
             f'    val Lg = {sp["lg"]}.dp', f'    val Xl = {sp["xl"]}.dp',
             f'    val Xxl = {sp["xxl"]}.dp', f'    val Section = {sp["section"]}.dp',
             "    val TouchTarget = 48.dp", f'    val CardRadius = {rad["card"]}.dp',
             f'    val SheetRadius = {rad["sheet"]}.dp', f'    val ChipRadius = {rad["chip"]}.dp',
             "    val ContentMaxWidth = 720.dp", "}", "",
             "object HermesElevation {",
             f'    val None = {el["none"]}.dp', f'    val Low = {el["low"]}.dp',
             f'    val Medium = {el["medium"]}.dp', f'    val High = {el["high"]}.dp',
             f'    val Drag = {el["drag"]}.dp', "}",
             "enum class HermesTextStyle { Display, TitleLarge, Title, BodyLarge, Body, Label, Caption, Code }"]
    return "\n".join(lines) + "\n"


def gen_motion(d):
    m = d["primitive"]["motion"]
    lines = ["package com.hermes.android.core.design.tokens", "",
             "import androidx.compose.animation.core.Easing",
             "import androidx.compose.animation.core.FastOutSlowInEasing",
             "import androidx.compose.animation.core.LinearEasing",
             "import androidx.compose.animation.core.LinearOutSlowInEasing", "",
             "object HermesMotion {",
             f'    val DurationInstant = {m["durationInstant"]}',
             f'    val DurationFast = {m["durationFast"]}',
             f'    val DurationBase = {m["durationBase"]}',
             f'    val DurationSlow = {m["durationSlow"]}',
             f'    val DurationEmphasis = {m["durationEmphasis"]}',
             "    val EaseStandard: Easing = FastOutSlowInEasing",
             "    val EaseDecelerate: Easing = LinearOutSlowInEasing",
             "    val EaseLinear: Easing = LinearEasing",
             "}", "",
             "data class ReducedMotion(val enabled: Boolean = false)"]
    return "\n".join(lines) + "\n"


def main():
    d = load()
    os.makedirs(TOK, exist_ok=True)
    for name, content in (
        ("ColorTokens.kt", gen_colors(d)),
        ("ShapeSpacingTokens.kt", gen_shape(d)),
        ("MotionTokens.kt", gen_motion(d)),
    ):
        with open(os.path.join(TOK, name), "w") as f:
            f.write(content)
        print(f"WROTE {name} ({content.count(chr(10))} lines)")


if __name__ == "__main__":
    main()
