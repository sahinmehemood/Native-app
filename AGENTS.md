# Agent Governance — Hermes Android Native

Read this file before changing the project.

## Non-negotiable boundaries

1. Android is a Hermes client, not a replacement for Hermes.
2. Hermes `config.yml` remains the authority for models, providers, and routing.
3. Do not add CCR, a duplicate model router, or a custom inference gateway.
4. Do not embed Python, Termux, the personal vault, provider credentials, or shell execution in the APK.
5. NOUS operations go through approved Hermes capabilities and require scoped user intent.
6. Never copy old API assumptions without verifying them against the pinned Hermes version.
7. Never implement a visual feature without an approved Figma frame or written design exception.
8. Never merge UI without phone, failure, accessibility, and reduced-motion verification.

## Agent work protocol

Every task must state its scope, acceptance criteria, owned paths, forbidden paths, and verification commands. Agents must make small changes, run checks, and report changed files.

No agent may silently change the protocol, security boundary, permissions, navigation model, or design tokens. Those require an architecture or design decision record.

## Definition of done

- Requirement and acceptance criteria are satisfied.
- Relevant tests pass.
- Error, offline, loading, and empty states are handled.
- Secrets are not introduced.
- Accessibility semantics are present.
- UI changes include screenshots or a Figma frame reference.
- Protocol changes include fixtures and compatibility notes.
- Documentation and handoff notes are updated.

