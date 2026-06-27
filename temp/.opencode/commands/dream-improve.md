---
description: Apply unapplied feature retrospectives to improve the Dream template
agent: dream-general
---

Apply all unapplied feature retrospectives to the Dream template. Use this when
you want the AI to learn from past feature generations and improve the template.

Read:
- `.opencode/guidance/opencode-agent-map.md`
- `.opencode/guidance/dream-agent-operating-guide.md`
- `.opencode/knowledge-graph/README.md`
- `.opencode/knowledge-graph/best-practices/README.md`
- relevant `.opencode/knowledge-graph/best-practices/*.md`

Then:
- find all `.features/*/retrospective.md` files where `Applied: not yet` in the Application Status section
- if none found, report that all retrospectives are applied and stop
- collect all recommended improvements across unapplied retros, grouped by target area:
  - guidance changes (`.opencode/guidance/`)
  - best-practices additions (`.opencode/knowledge-graph/best-practices/`)
  - anti-patterns (existing or new entry in `26-anti-patterns-to-avoid-when-reusing-this-architecture.md`)
  - template code changes (starter project files)
  - prompt or workflow changes (`.opencode/guidance/prompts/`, commands)
- for each group, apply the recommended changes to the corresponding template files
- update each retro: set `Applied:` to the current date and `What was updated:` to a summary of changes made
- run `./gradlew :androidApp:assembleDebug` and `./gradlew allTests` (or at minimum `./gradlew projects`) to verify the template is still structurally valid after changes
- run `git status`, `git diff`, and `git log --oneline -10`
- present the commit summary to the user and request explicit confirmation before staging and committing template improvement files

Do not apply changes that would break the current project. If a recommendation
is ambiguous, skip it and note why in the retro. Do not commit until the user explicitly approves.
