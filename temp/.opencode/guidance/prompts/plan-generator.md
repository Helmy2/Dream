# Plan Generator Prompt

Use this prompt scaffold when generating `.features/<feature-name>/plan.md` during Phase 0 (Plan & User Review).

```text
Intended OpenCode agent: dream-general

You are generating a Dream feature plan.

Your task:
Convert the user's feature request into a structured plan document that will be presented
to the user for review. The plan must be clear enough that the user can understand exactly
what Dream will build, what files will be touched, and in what order.

Inputs you must use:
- user request
- `.features/app-contract.yaml`
- `.features/decisions.yaml` if present
- `AGENTS.md`
- relevant architecture rules from `.opencode/knowledge-graph/`
- `dream-discovery` (deep mode) output for project structure context (uses codebase-memory-mcp for efficient code intelligence queries)

Rules:
- Keep the plan concise but complete — the user needs to understand scope before approving
- Every section must be filled — use "None" or "TBD" explicitly rather than omitting
- Screens must list all states: Loading, Empty, Content, Error
- Architecture decisions must be concrete (e.g., "Room" not "some database", "DataStore" not "preferences")
- Include exact module paths so the user can verify scope
- Map phases to the correct Dream subagents
- Do not include implementation details in the plan — it describes WHAT, not HOW
- The plan is NOT a contract — it's a structural overview for user approval

Template to use:
`.opencode/guidance/templates/plan-template.md`

Output format:
Write the completed plan to `.features/<feature-name>/plan.md` using the template structure.

After writing:
1. Present the plan to the user in the conversation (readable formatting, not raw markdown)
2. Ask explicitly:
   - "Do you have any **questions** about this plan?"
   - "Would you like to **edit** any part?"
   - "Do you **confirm** this plan so I can proceed?"
   - "Or would you like to **deny** this plan and stop?"
3. Handle user response:
   - Question → answer, update plan if needed, re-present
   - Edit → update `.features/<feature-name>/plan.md`, re-present
   - Confirm → proceed to next phase
   - Deny → stop and document in `.features/<feature-name>/report.md`
```
