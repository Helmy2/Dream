# Verification And Repair Prompt

Use this prompt scaffold when verifying or repairing a Dream-generated project.

```text
Primary OpenCode agents:
- general for orchestration and reruns
- implementer for fixes
- inspector for final QA gate on substantial work

You are verifying and repairing a Dream-generated Kotlin Multiplatform project.

Your task:
Run the required verification flow, diagnose failures, repair them while preserving architecture rules, and produce a final verification result.

Read first:
- `knowledge-graph/PRODUCT.md`
- `guidance/opencode-agent-map.md`
- `AI_MVP_BUILDER_PLAN.md`
- relevant graph-backed rules for the failing module or runtime surface
- `.mvp/app-contract.yaml`
- `.mvp/decisions.yaml`
- `.mvp/features/<feature-name>/contract.yaml` if feature-specific

Required verification for local-only MVPs:
- `./gradlew allTests`
- `./gradlew :androidApp:assembleDebug`
- `./gradlew :desktopApp:run`

Required behavior:
- do not report success until verification is green or a blocker is clearly documented
- preserve `Presentation -> Domain <- Data`
- do not fix failures by collapsing module boundaries
- do not move business logic into host apps as a shortcut
- do not ignore hardcoded shared UI strings, direct system time access, or swallowed `CancellationException`

Repair loop:
1. `general` runs verification
2. `general` identifies the failing module or runtime layer
3. `dream-explore` or `dream-context-scout` gathers additional focused context if needed
4. `implementer` loads relevant rules from the guidance and graph
5. `implementer` makes the smallest correct fix
6. `general` reruns verification
7. repeat until green or blocked
8. `inspector` performs final review for substantial generation or repair work before validated success is reported

Output requirements:
- produce a concise final verification summary
- state whether status is `validated` or `blocked`
- list commands run
- list failing areas if blocked
- write or update `.mvp/verification-report.md`

If blocked:
- document the blocker precisely
- distinguish environment blockers from code blockers
- do not claim success
```
