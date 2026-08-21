# Hermes Android Native

A native Android client for Hermes Agent, designed around the Hermes Desktop experience and redesigned for touch, small screens, tablets, and foldables.

## Product boundary

This app is a secure client for a Hermes gateway. Hermes remains authoritative for models, routing, skills, tools, agents, memory, cron, and NOUS operations.

This project does not contain CCR, a duplicate router, a second inference gateway, a WebView desktop clone, or a separate Android agent runtime.

## Current status

Phase 0: repository and protocol discovery.

Implementation begins only after the running Hermes version and gateway contract are verified.

## Working rules

- Figma is the visual source of truth.
- Hermes gateway behavior is the protocol source of truth.
- Every feature requires loading, empty, error, offline, reconnect, accessibility, and reduced-motion states where applicable.
- Every network action must be cancellable, retry-safe, and observable.
- Dangerous actions require explicit approval and never auto-approve after reconnect.
- Personal vault contents and credentials never enter this repository.

## Planned stack

Kotlin, Jetpack Compose, Coroutines/Flow, Room, DataStore, Android Keystore, authenticated Hermes WebSocket/JSON-RPC, REST where required, WorkManager only for permitted client work, and GitHub Actions.

