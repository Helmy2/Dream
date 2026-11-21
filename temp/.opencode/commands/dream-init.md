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
    - Check `xcodebuild -version` (macOS only; required by the Toolchain for the iOS app)
    - Check `./kotlin --version` (Kotlin Toolchain wrapper)
    - Check `xcrun simctl list` (macOS only)
    - Check `codebase-memory-mcp --version` (optional, for code intelligence)
    - Check Maestro via `/dream-e2e` preflight when needed
    - Report each as "passed" or "blocked by environment"

4. **`specs/` directory structure**:
   - Create `specs/` if missing
   - Create `specs/app-contract.yaml` with a stub:
     ```yaml
     # App Contract
     name: <project-name-or-uninitialized>
     features: []
     ```
   - Create `specs/decisions.yaml` with a stub:
     ```yaml
     # Architecture Decisions
     ```
   - Create `specs/` directory
   - Report "specs/: initialized"

5. **Build verification**:
   - Run `./kotlin build -m core-domain --platform=android` to verify the Kotlin Toolchain build is structurally valid
   - If this fails, report the error as a build environment blocker (do not interpret Toolchain output as a product defect at this stage)
   - Report "build: passed" or "build: <error summary>"

6. **Readiness report** — Output a summary table:
    ```
    | Check                    | Status        |
    |--------------------------|---------------|
    | git                      | initialized   |
    | Android SDK              | /path         |
    | Maestro (via `/dream-e2e`) | available/blocked | |
    | Xcode                    | passed        |
    | JDK                      | 17.0.x        |
    | codebase-memory-mcp      | passed        |
    | specs/               | initialized   |
    | Kotlin Toolchain build   | passed        |
    | Models                   | current session model / custom |
    ```

7. **Model configuration** — Ask the user which models Dream should use. Use the `question` tool with these two options, recommended first:
   - **Current model (recommended, default)**: every agent uses the model selected in this session. Write nothing to `opencode.json`; subagents without a pinned model inherit the model of the agent that called them.
   - **Custom**: ask for a model per agent, and use `opencode models` to list valid IDs
   - Only for custom: write the choice to `.opencode/opencode.json` under the `agent` key, and confirm every model ID exists in `opencode models` first
   - Never overwrite an existing `agent` block without showing it to the user and getting a yes
   - Report "models: current session model" or "models: custom" in the readiness table
   - If the chosen model has no image input, warn that `dream-tester` and `dream-inspector` cannot read screenshots; they must rely on `mobile_list_elements_on_screen` and crash logs, and the report must say which visual checks were not run

### Notes

- This command is safe to re-run — it skips steps that are already complete
- Do not generate any feature code during init
- Do not commit unless the user explicitly asks for it after reviewing the readiness report
- Write results to `specs/init-report.md` at the end
