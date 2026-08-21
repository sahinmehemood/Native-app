#!/usr/bin/env python3
"""
figma_sync.py — Figma variables -> Hermes Android Compose design tokens.

Reads a Figma file's local variables via the REST API and emits Kotlin token
files into core/design. This is the bridge that makes Figma the visual source
of truth (per README.md / QUALITY-GATES.md).

Usage:
    FIGMA_TOKEN=figd_xxx python3 scripts/figma_sync.py <FILE_KEY>

Requires: the Figma file to expose variables (primitive + semantic collections
with Dark/Light modes). See docs/FIGMA-DESIGN-SPEC.md for the expected schema.

Output:
    core/design/src/main/java/com/hermes/android/core/design/tokens/ColorTokens.kt
    (semantic Dark/Light from the Semantic/Color collection modes)
"""
import json, os, sys, urllib.request

BASE = "https://api.figma.com/v1"
OUT = os.path.join(os.path.dirname(__file__), "..",
                   "core/design/src/main/java/com/hermes/android/core/design/tokens/ColorTokens.kt")


def api(token, path):
    req = urllib.request.Request(f"{BASE}{path}", headers={"X-Figma-Token": token})
    with urllib.request.urlopen(req, timeout=30) as r:
        return json.load(r)


def hex_from_var(variable, mode_id):
    """Resolve a variable's value for a mode (handles aliases)."""
    vals = variable.get("valuesByMode", {})
    v = vals.get(mode_id)
    if isinstance(v, dict) and v.get("type") == "VARIABLE_ALIAS":
        return ("__alias__", v.get("id"))
    return ("__color__", v)


def main():
    token = os.environ.get("FIGMA_TOKEN")
    if not token:
        print("ERROR: set FIGMA_TOKEN", file=sys.stderr); sys.exit(2)
    if len(sys.argv) < 2:
        print("ERROR: pass FILE_KEY", file=sys.stderr); sys.exit(2)
    key = sys.argv[1]

    data = api(token, f"/files/{key}/variables/local")
    variables = {v["id"]: v for v in data.get("variables", {}).values()}
    collections = data.get("variableCollections", {})

    # Find semantic color collection with Dark/Light modes
    sem_coll = None
    for cid, c in collections.items():
        if c.get("variableIds") and any(
            variables.get(vid, {}).get("name", "").lower().startswith("color")
            for vid in c["variableIds"][:3]
        ):
            sem_coll = c; break
    if not sem_coll:
        print("ERROR: no semantic color collection found", file=sys.stderr); sys.exit(3)

    modes = sem_coll["modes"]  # [{modeId, name}]
    dark = next((m for m in modes if m["name"].lower() == "dark"), modes[0])
    light = next((m for m in modes if m["name"].lower() == "light"), modes[-1])

    def resolve_name_value(vid, mode):
        v = variables.get(vid, {})
        kind, val = hex_from_var(v, mode["modeId"])
        if kind == "__alias__":
            tgt = variables.get(val, {})
            _, val = hex_from_var(tgt, mode["modeId"])
        name = v.get("name", "unknown").split("/")[-1].replace(" ", "")
        return name, (val or "0xFF000000")

    dark_map = {resolve_name_value(vid, dark) for vid in sem_coll["variableIds"]}
    light_map = {resolve_name_value(vid, light) for vid in sem_coll["variableIds"]}

    lines = ["package com.hermes.android.core.design.tokens", "", "import androidx.compose.ui.graphics.Color", "",
             "object HermesColorTokens {", "    object Dark {"]
    lines += [f'        val {n} = Color({v})' for n, v in sorted(dark_map)]
    lines += ["    }", "    object Light {"]
    lines += [f'        val {n} = Color({v})' for n, v in sorted(light_map)]
    lines += ["    }", "}"]
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w") as f:
        f.write("\n".join(lines) + "\n")
    print(f"WROTE {OUT} ({len(dark_map)} dark / {len(light_map)} light tokens)")


if __name__ == "__main__":
    main()
