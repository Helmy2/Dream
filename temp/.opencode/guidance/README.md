# Dream Guidance

This folder contains the operational guidance layer for Dream agents.

The knowledge graph stores reusable architectural rules.
The template stores runnable starting code.
This folder tells OpenCode agents how to use both consistently.

## Files

- `opencode-agent-map.md`: stage-to-agent ownership map for Dream
- `dream-agent-operating-guide.md`: default workflow and non-negotiable operating rules
- `prompts/project-instantiation.md`: prompt scaffold for copying and customizing a Dream template into a new project
- `prompts/app-contract-generator.md`: prompt scaffold for generating `.features/app-contract.yaml`
- `prompts/feature-contract-generator.md`: prompt scaffold for generating `.features/<feature>/contract.yaml`
- `prompts/feature-implementation.md`: prompt scaffold for implementing a feature inside a Dream project
- `prompts/verification-and-repair.md`: prompt scaffold for verification, diagnosis, and repair

## Intended Use

OpenCode agents working on Dream should read these files in order:

1. `opencode-agent-map.md`
2. `dream-agent-operating-guide.md`

Then they should use the appropriate prompt scaffold for the current stage.
