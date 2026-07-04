---
description: Initialize a Dream project (git, Android SDK, tooling check)
agent: dream-general
---

Initialize the project environment for Dream development. Run this once after copying the template.

Treat `$ARGUMENTS` as optional override hints (e.g., app name, package name).

### Steps

1. **Git initialization** — If the project is not yet a git repository:
   - Run `git init`
   - Create an initial `.gitignore` from the template defaults if one doesn't exist
   - Stage all files and create an initial commit with message `init: Dream project scaffold`
   - Verify `git status`, `git diff`, and `git log --oneline -10` all work
   - Report "git: initialized" or "git: already a repository"

2. **Android SDK detection** — Check for a usable Android SDK:
   - Check `ANDROID_HOME`, then `ANDROID_SDK_ROOT`, then `~/Library/Android/sdk`
   - If found and `local.properties` is missing or has the wrong path, write `sdk.dir=<path>` to `local.properties`
   - Verify `adb` is available at `$SDK/platform-tools/adb`
   - Report "Android SDK: <path>" or "blocked: Android SDK not found"

3. **Platform tooling verification**:
    - Check `java -version` (JDK 17+ required)
    - Check `maestro --version` (Maestro CLI)
    - Check `xcodebuild -version` (macOS only)
    - Check `xcrun simctl list` (macOS only)
    - Check `codebase-memory-mcp --version` (optional, for code intelligence)
    - Report each as "passed" or "blocked by environment"

4. **`.features/` directory structure**:
   - Create `.features/` if missing
   - Create `.features/app-contract.yaml` with a stub:
     ```yaml
     # App Contract
     name: <project-name-or-uninitialized>
     features: []
     ```
   - Create `.features/decisions.yaml` with a stub:
     ```yaml
     # Architecture Decisions
     ```
   - Create `.features/` directory
   - Report ".features/: initialized"

5. **Build verification**:
   - Run `./gradlew projects` to verify the Gradle build is structurally valid
   - If this fails, report the error as a build environment blocker (do not interpret Gradle output as a product defect at this stage)
   - Report "build: passed" or "build: <error summary>"

6. **Readiness report** — Output a summary table:
    ```
    | Check                    | Status        |
    |--------------------------|---------------|
    | git                      | initialized   |
    | Android SDK              | /path         |
    | Maestro                  | passed        |
    | Xcode                    | passed        |
    | JDK                      | 17.0.x        |
    | codebase-memory-mcp      | passed        |
    | .features/               | initialized   |
    | Gradle projects          | passed        |
    | Models                   | configured    |
    ```

7. **Model configuration** — Ask the user if they want to customize model assignments:
   - Present the default model mapping (see Model Assignments below)
   - Offer presets:
     - **Balanced** (default): DeepSeek V4 Pro for orchestrator/implementer/inspector, Qwen3.7 Plus for designer/UI, Big Pickle for discovery/research, MiMo V2.5 for tester
     - **Budget**: Big Pickle for all agents (free, 200K context)
     - **Maximum quality**: DeepSeek V4 Pro for all agents (strongest reasoning)
     - **Custom**: Let user pick per-agent
   - If user chooses custom, walk through each agent and ask for model selection
   - Write the selected configuration to `.opencode/opencode.json` under the `agent` key
   - Report "models: configured" with the selected preset name

### Model Assignments

Default model mapping optimized per agent role:

| Agent | Model | Model ID | Context | Images | Cost ($/M in/out) |
|---|---|---|---|---|---|
| `dream-general` | DeepSeek V4 Pro | `opencode-go/deepseek-v4-pro` | 1M | No | $1.74/$3.48 |
| `dream-designer` | Qwen3.7 Plus | `opencode-go/qwen3.7-plus` | 1M | Yes | $0.40/$1.60 |
| `dream-discovery` | Big Pickle | `opencode/big-pickle` | 200K | No | FREE |
| `dream-implementer` | DeepSeek V4 Pro | `opencode-go/deepseek-v4-pro` | 1M | No | $1.74/$3.48 |
| `dream-implementer-ui` | Qwen3.7 Plus | `opencode-go/qwen3.7-plus` | 1M | Yes | $0.40/$1.60 |
| `dream-research` | Big Pickle | `opencode/big-pickle` | 200K | No | FREE |
| `dream-tester` | MiMo V2.5 | `opencode-go/mimo-v2.5` | 1M | Yes | $0.14/$0.28 |
| `dream-inspector` | DeepSeek V4 Pro | `opencode-go/deepseek-v4-pro` | 1M | No | $1.74/$3.48 |

To override, edit `.opencode/opencode.json` or re-run `/dream-init`.

### Notes

- This command is safe to re-run — it skips steps that are already complete
- Do not generate any feature code during init
- Do not commit unless the user explicitly asks for it after reviewing the readiness report
- Write results to `.features/init-report.md` at the end
