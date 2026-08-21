# Hermes Mobile API Contract

Status: discovery required.

This document must be completed from the pinned Hermes version before production client code is written.

Required evidence:

- Gateway URL and authentication flow
- WebSocket path and framing
- JSON-RPC request/response correlation
- Capability and protocol version discovery
- Session lifecycle methods
- Prompt, queue, steer, and interrupt behavior
- Message, thinking, tool, approval, clarification, secret, and sub-agent events
- File and preview payloads
- Reconnect and resume semantics
- Idempotency and duplicate-prompt prevention
- Error codes and compatibility behavior
- Remote execution labeling

The client must tolerate unknown fields and events, but must not invent behavior for unknown commands.

