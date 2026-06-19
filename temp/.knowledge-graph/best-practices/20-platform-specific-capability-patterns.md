# 20. Platform-Specific Capability Patterns

This architecture style supports good patterns for hardware and platform services.

### 19.1 Abstract platform capabilities behind shared contracts

Examples in this app include:

- location
- notifications
- encryption
- compass sensors

Best practice:

- define the contract in shared code
- provide platform implementations separately
- inject them through DI

### 19.2 Native UI is acceptable for performance-sensitive or platform-heavy cases

The project history around Qibla and the final native SwiftUI architecture both support the same lesson:

- use native UI where platform fidelity or performance warrants it
- do not overcommit to shared UI if it makes platform behavior worse
