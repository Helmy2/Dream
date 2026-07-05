# Verification Report Template

Use this exact status vocabulary for every check: `passed`, `blocked by environment`, `not run`, or `not applicable`.

## Summary

- Overall status: `<validated | partial | blocked>`
- Review mode: `<delegated | manual | not run>`
- Commit: `<hash | not created: reason>`
- Date: `<YYYY-MM-DD>`

## Passed

- `<check>`: `<command or evidence>`

## Blocked By Environment

- `<check>`: `<tool/platform/setup blocker and actionable setup note>`

## Not Run

- `<check>`: `<reason it was not run>`

## Not Applicable

- `<check>`: `<reason it does not apply to this app contract>`

## Commands Run

```bash
<command>
```

## Contract And Review

- Contract parity: `<passed | partial | blocked>`
- Required tests: `<passed | partial | blocked>`
- Inspector result: `<passed | failed | blocked | not run>`
- Manual review fallback: `<not needed | summary>`

## Notes

- `<important caveat, blocker, or follow-up>`

## Template Feedback

- Retrospective needed: `<yes | no>`
- Retrospective path: `<features/template-improvement-retrospective.md | not applicable>`
- Reusable template lessons: `<short summary or not applicable>`
