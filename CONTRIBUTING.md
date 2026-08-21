# Contributing

Work from a feature branch. Start with an issue or written task, keep changes focused, and open a draft pull request until build, test, security, and design checks are complete.

Required local checks:

```text
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

Visual changes require a Figma frame, screenshots, accessibility verification, and explicit handling for loading, empty, error, offline, and reduced-motion states.

