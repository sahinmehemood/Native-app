# Quality Gates

## Pull request checks

- Gradle build
- Kotlin formatting and static analysis
- Android lint
- Unit and repository tests
- Gateway contract tests
- Compose UI tests
- Accessibility semantics checks
- Secret scan
- Dependency vulnerability scan
- Architecture boundary check

## UI checks

- Approved Figma frame reference
- Phone portrait and landscape
- Tablet/foldable where relevant
- Dark theme
- Large font scale
- Reduced motion
- Loading, empty, error, offline, reconnect, and permission states
- Screenshot regression comparison

## Hermes-specific checks

- Streaming deltas do not cause excessive recomposition.
- Approval and interrupt controls remain responsive during long streams.
- Reconnect never auto-resends a prompt or auto-approves an action.
- Sessions recover without losing drafts.
- Remote host and workspace are visible.
- NOUS writes are scoped and auditable.
- Logs never expose credentials, authorization headers, or private paths.

