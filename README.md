# Dream

Dream is a local-first AI app builder for Kotlin Multiplatform. It packages a reusable starter project, OpenCode agents, architecture guidance, verification rules, and smoke-test scaffolding so a natural-language product request can become a modular Android and iOS app.

## Repository Structure

- `temp/` — Canonical Kotlin Multiplatform starter project copied by the initializer
- `temp/features/` — Generated app contracts, feature specs, decisions, reports, and retrospectives
- `temp/maestro/` — Android and iOS smoke-flow scaffolding
- `dream-init` — Initializer script that copies and renames `temp/` into a new project
- `ONBOARDING.md` — Human-readable guide to the project architecture, workflow, and validation model
- `AGENTS.md` — Repository-level working rules for agents and contributors

## Quick Start

```bash
# Create a new project from the template
./dream-init ../my-app

# Or work directly in the template
cd temp
./.opencode/scripts/dream-preflight.sh
```

## Documentation

Read in order:

1. `ONBOARDING.md` — Architecture, workflow, and validation model
2. `temp/AGENTS.md` — Starter project agent instructions
3. `temp/.opencode/guidance/opencode-agent-map.md` — Agent roles
4. `temp/.opencode/guidance/dream-agent-operating-guide.md` — Operating workflow
5. `temp/.opencode/knowledge-graph/README.md` — Architecture rules and source graph

## Status

Active development. See `ONBOARDING.md` for the full mental model and repository map.
