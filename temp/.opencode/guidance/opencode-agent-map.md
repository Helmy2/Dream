# Dream OpenCode Agent Map

## Purpose

Defines which OpenCode agent owns each Dream workflow stage.

## Available OpenCode Agents

### Orchestrator

- `dream-general`: Primary orchestrator — interprets requests, breaks work into phases, delegates to subagents, manages context, and reports final status.

### Read-Only Subagents

- `dream-discovery`: Fast codebase exploration and context building — locates files, symbols, builds focused context packs. Modes: `explore` (quick/deep), `context`.
- `dream-research`: Fast codebase discovery and external research — locates modules, files, symbols, builds context packs, and fetches external documentation/APIs. Modes: `codebase` (quick/deep), `external` (docs/APIs/troubleshooting), `combined`.

### Spec-Generation Subagent

- `dream-designer`: Translates feature contracts into cross-platform UI/UX specs. Writes spec files, not code.

### Code-Writing Subagents

- `dream-implementer`: KMP business foundation — domain, data, api, wiring. No UI.
- `dream-implementer-ui`: Cross-platform UI implementation — Jetpack Compose (Android) and SwiftUI (iOS). Receives `platform` parameter: `android`, `ios`, or `both`.

### Verification Subagent

- `dream-tester`: Full verification ladder — tests, builds, device, logs.

### Review Subagent

- `dream-inspector`: Final QA and architecture review gate.

## Model Assignments

Each agent is configured with a model optimized for its role. Configuration lives in `opencode.json` under the `agent` key. Users can customize during `/dream-init` or by editing `opencode.json` directly.

| Agent | Model | Model ID | Rationale |
|---|---|---|---|
| `dream-general` | DeepSeek V4 Pro | `opencode-go/deepseek-v4-pro` | 1M context + strongest reasoning for orchestration |
| `dream-designer` | Qwen3.7 Plus | `opencode-go/qwen3.7-plus` | Image support for visual understanding |
| `dream-discovery` | Big Pickle | `opencode/big-pickle` | Free, 200K context for fast exploration |
| `dream-implementer` | DeepSeek V4 Pro | `opencode-go/deepseek-v4-pro` | Strongest reasoning for complex KMP code |
| `dream-implementer-ui` | Qwen3.7 Plus | `opencode-go/qwen3.7-plus` | Image support for UI spec interpretation |
| `dream-research` | Big Pickle | `opencode/big-pickle` | Free, 200K context for research |
| `dream-tester` | MiMo V2.5 | `opencode-go/mimo-v2.5` | Low cost, image support for screenshot comparison |
| `dream-inspector` | DeepSeek V4 Pro | `opencode-go/deepseek-v4-pro` | Strongest reasoning for QA gate |

### Overriding Models

Edit `opencode.json` to change any agent's model:

```json
{
  "agent": {
    "dream-general": {
      "model": "opencode-go/deepseek-v4-pro"
    }
  }
}
```

Run `opencode models` to see all available models.

## MCP Tools

Two MCP servers are configured in `opencode.json`:

### mobile-mcp

Device interaction for verification and UI inspection.

- **Used by**: `dream-tester`, `dream-inspector`
- **Key tools**: `mobile_take_screenshot`, `mobile_list_elements_on_screen`, `mobile_list_crashes`, `mobile_get_crash`
- **Launcher**: `.opencode/scripts/start-mobile-mcp.sh`

### codebase-memory-mcp

Code intelligence via persistent knowledge graph. Complements the prescriptive knowledge graph (rules/constraints) with descriptive code intelligence (what code actually does).

- **Used by**: `dream-discovery`, `dream-research`, `dream-implementer`, `dream-implementer-ui`, `dream-inspector`
- **Key tools**: `search_graph`, `trace_path`, `get_code_snippet`, `detect_changes`, `get_architecture`, `query_graph`
- **Launcher**: `.opencode/scripts/start-codebase-memory-mcp.sh`
- **Reference**: `.opencode/guidance/reference/codebase-memory-mcp-tools.md`

**Rule**: Agents should prefer MCP queries over grep/read for codebase exploration. Fall back to grep/glob only for string literals, non-code files, or when MCP returns insufficient results.

## Default Ownership

### `dream-general`

Owns: workflow orchestration, contracts, **plan generation**, delegation, verification coordination.

Should do:
- Interpret user request
- **Evaluate complexity** (trivial/simple/complex) and select appropriate workflow
- **Create the plan** (`.features/<feature>/plan.md` for complex, conversation plan for simple/trivial) and present it to the user during Phase 0
- **Ask for questions, edits, or confirmation before proceeding**
- Launch appropriate subagent per phase
- Ensure Dream doesn't report success before verification is green

Should not: edit implementation files, run verification directly, skip inspector for substantial work, **skip Phase 0 (Plan & User Review)**.

### `dream-research`

Owns: fast codebase discovery and external research.

Modes:
- **codebase quick**: Find specific files, symbols, patterns. Return paths only.
- **codebase deep**: Build focused context pack for a task. Return paths + summaries + key snippets.
- **external**: Fetch external documentation, API references, troubleshoot errors.
- **combined**: Both codebase and external research in one pass.

Use when: locating something, building context before implementation or repair, researching external libraries or APIs.

### `dream-designer`

Owns: UI/UX specification generation.

Use when: feature contract exists and UI implementation is about to start.

Output: `.features/<feature>/ui-spec.md`

### `dream-implementer`

Owns: KMP business layer — domain, data, api, wiring.

Use when: creating or repairing KMP business layer.

Must preserve: `Presentation -> Domain <- Data`, pure Kotlin domain.

### `dream-implementer-ui`

Owns: cross-platform UI implementation — Jetpack Compose (Android) and SwiftUI (iOS).

Use when: creating or updating UI screens, ViewModels, bridge code.

Receives `platform` parameter: `android`, `ios`, or `both`.

Must: read UI spec, use Dream design system, implement all states, load platform-specific best practices.

### `dream-tester`

Owns: full verification ladder.

Use when: verification needed after implementation or repair.

Must: run preflights, record status buckets, inspect platform logs.

### `dream-inspector`

Owns: architecture and behavior review.

Use when: substantial feature generated or repair claims green status.

Output: `APPROVED` or itemized defect list.

## Default Workflow Map

### Project Creation

1. `dream-general` interprets the request.
2. `dream-research` gathers relevant context if needed.
3. **Phase 0: `dream-general` produces `.features/<feature>/plan.md`, presents it to the user, and gets explicit confirmation before proceeding.**
4. `dream-general` generates contracts.
5. `dream-implementer` instantiates template.
6. `dream-implementer` removes `feature/sample` after first real feature.

### Feature Generation

1. `dream-general` selects feature to generate.
2. `dream-general` checks for command trigger:
   - If `/dream-create` or `/dream-feature` → COMPLEX (skip evaluation)
   - Otherwise: `dream-general` **evaluates complexity** (trivial/simple/complex).
3. Workflow selected based on complexity:
   - **Trivial**: `/dream-quick-fix` (2-line plan, no artifacts except verification-report.md)
   - **Simple**: Lightweight workflow (brief conversation plan, minimal artifacts)
   - **Complex**: Full `/dream-feature` workflow
4. For complex: `dream-research` gathers context (deep mode).
5. For complex: **Phase 0: `dream-general` produces `.features/<feature>/plan.md`, presents it to the user, and gets explicit confirmation.**
6. For complex: `dream-general` produces feature contract.
7. For complex: `dream-research` if external research needed.
8. For complex: `dream-designer` produces UI spec.
9. For complex: `dream-implementer` generates KMP business layer.
10. For complex: `dream-implementer-ui` generates UI layer (platform: both).
11. `dream-tester` runs verification ladder.
12. For complex: `dream-inspector` reviews substantial work.

### UI Fix

1. `dream-general` interprets the UI issue description.
2. **Phase 0: `dream-general` produces a fix plan, presents it to the user, and gets explicit confirmation.**
3. `dream-tester` captures screenshot + element tree via mobile-mcp (Phase 1: Capture).
4. `dream-research` identifies affected UI source files (Phase 2: Diagnose).
5. `dream-implementer-ui` applies fix (Phase 3: Fix).
6. `dream-tester` re-captures screenshot + element tree for before/after comparison (Phase 4: Verify).

### Bug Fix

1. `dream-general` interprets the bug description or crash log.
2. **Phase 0: `dream-general` produces a fix plan with suspected root-cause layer, gets user confirmation.**
3. `dream-tester` gathers diagnostics (crash logs, logcat, build output) + `dream-research` for error research (Phase 1: Diagnostics).
4. `dream-research` narrows to affected files; `dream-general` identifies failing layer (Phase 2: Root Cause).
5. Layer-appropriate implementer applies fix (Phase 3: Fix).
6. `dream-tester` runs targeted re-verification per fixed layer (Phase 4: Verify).

### Verification And Repair

1. `dream-general` selects failing surface.
2. `dream-research` narrows failure surface if needed.
3. Relevant implementer applies minimal fix.
4. `dream-tester` reruns verification.
5. `dream-inspector` final review gate.

### Plan Review (Phase 0 in all workflows)

Introduced before every generation or creation workflow:
1. `dream-general` gathers context (project, existing features, rules).
2. `dream-general` produces `.features/<feature>/plan.md` using the plan template.
3. `dream-general` presents the plan to the user in the conversation.
4. `dream-general` asks for questions, edits, or confirmation.
5. On **Confirm**: proceed to next phase. On **Edit**: update plan, re-present. On **Deny**: stop and document.

### Inspector Repair Loop

When `dream-inspector` returns an itemized defect list:
1. `dream-general` presents defects to user with severity: **critical** / **warning** / **info**.
2. User decides: **repair? accept with defects? deny?**
3. On **repair**: delegate to appropriate implementer, re-run inspector.
4. On **accept**: document defects in report, proceed to merge.
5. On **deny**: stop workflow, document reason.
6. Max 2 inspector repair rounds — if still failing after 2 rounds, escalate to user.

## Handoff Rules

- `dream-general` → `dream-research` for discovery (not broad search itself)
- `dream-general` → `dream-research` for external research
- `dream-general` → `dream-designer` for UI spec
- `dream-general` → `dream-implementer` for KMP business layer
- `dream-general` → `dream-implementer-ui` for UI layer (platform: android, ios, or both)
- `dream-general` → `dream-tester` for verification
- `dream-general` → `dream-inspector` for review
- `dream-general` → `dream-implementer-ui` for UI fix (platform-specific)
- `dream-general` → `dream-implementer` / `dream-implementer-ui` for bug fix (layer-appropriate)
- `dream-general` → `dream-implementer` / `dream-implementer-ui` for inspector repair (layer-appropriate)

## Phase 0 Handoff

Phase 0 is owned entirely by `dream-general`. It does not delegate to subagents — the plan is produced by `dream-general` itself, drawing from `dream-discovery` context if needed. The user interaction (present, question, confirm) happens directly in the conversation.
