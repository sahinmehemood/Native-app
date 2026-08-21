// Figma plugin — generates the Hermes Android design system into the current file.
// Mirrors design-system/hermes-tokens.json so Figma and Compose stay in lockstep.
// Run: open a blank Figma file -> Plugins -> Development -> "Hermes Token Generator".

const TOKENS = {
  primitive: {
    violet500: "#7C5CFF", violet900: "#2A2350", white: "#FFFFFF",
    green400: "#3DDC84", amber400: "#FFB020", red400: "#FF5C5C", blue400: "#5AC8FA",
    gray400: "#8A8A99", gray500: "#6A6A78",
    ink900: "#0B0B10", ink850: "#16161E", ink800: "#1F1F2A", ink750: "#242433", ink700: "#2E2E3C",
  },
  semanticDark: {
    accent: "violet500", accentSoft: "violet900", accentOn: "white",
    success: "green400", warning: "amber400", danger: "red400", info: "blue400",
    background: "ink900", surface: "ink850", surfaceVariant: "ink800",
    surfaceRaised: "ink750", border: "ink700", onSurface: "white",
    onSurfaceMuted: "gray400", onSurfaceSubtle: "gray500",
  },
  semanticLight: {
    accent: "violet500", accentSoft: "violet900", accentOn: "white",
    success: "green400", warning: "amber400", danger: "red400", info: "blue400",
    background: "paper50", surface: "white", surfaceVariant: "paper200",
    surfaceRaised: "paper300", border: "paper400", onSurface: "inkOnPaper",
    onSurfaceMuted: "grayOnPaper", onSurfaceSubtle: "gray400",
  },
};

async function createCollections() {
  // Primitive collection (no modes)
  const prim = figma.variables.createVariableCollection("Primitive/Color");
  const primMode = prim.modes[0].modeId;
  for (const [name, hex] of Object.entries(TOKENS.primitive)) {
    const v = figma.variables.createVariable(`color/${name}`, prim, "COLOR");
    const vv = figma.variables.createVariable(name, prim, "COLOR"); // alias holder
    vv.setValueForMode(primMode, hex);
    v.setValueForMode(primMode, { type: "VARIABLE_ALIAS", id: vv.id });
  }
  // Semantic collection with Dark/Light modes
  const sem = figma.variables.createVariableCollection("Semantic/Color");
  const darkMode = sem.modes[0].modeId;
  const lightMode = sem.addMode("Light").modeId;
  for (const name of Object.keys(TOKENS.semanticDark)) {
    const v = figma.variables.createVariable(`color/${name}`, sem, "COLOR");
    const primName = TOKENS.semanticDark[name];
    const primVar = prim.variableIds.map(id => figma.variables.getVariableById(id))
      .find(x => x.name === `color/${primName}`) || null;
    if (primVar) {
      v.setValueForMode(darkMode, { type: "VARIABLE_ALIAS", id: primVar.id });
      const lightPrimName = TOKENS.semanticLight[name];
      const lightPrim = prim.variableIds.map(id => figma.variables.getVariableById(id))
        .find(x => x.name === `color/${lightPrimName}`) || primVar;
      v.setValueForMode(lightMode, { type: "VARIABLE_ALIAS", id: lightPrim.id });
    }
  }
  return { prim, sem };
}

async function createComponents() {
  const names = ["Button/Primary", "Button/Secondary", "Button/Ghost",
    "Card/Surface", "Chip/Status", "MessageBubble/User", "MessageBubble/Assistant",
    "Sheet/Bottom", "StateEmpty", "StateError", "StateOffline", "StateLoading", "StateReconnect"];
  const page = figma.currentPage;
  for (const n of names) {
    const frame = figma.createFrame();
    frame.name = n;
    frame.resize(200, 120);
    const label = figma.createText();
    label.characters = n.split("/").pop();
    label.fontSize = 14;
    frame.appendChild(label);
    label.x = 12; label.y = 12;
  }
}

figma.showUI(__html__);
figma.ui.onmessage = async (msg) => {
  if (msg.type !== "generate") return;
  try {
    await createCollections();
    await createComponents();
    figma.ui.postMessage({ type: "done", msg: "Design system generated. Semantic/Color has Dark + Light modes; components created on the canvas." });
  } catch (e) {
    figma.ui.postMessage({ type: "done", msg: "Error: " + e.message });
  }
};
