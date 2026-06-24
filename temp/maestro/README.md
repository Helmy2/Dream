# Maestro Smoke Flows

Dream uses Maestro for black-box smoke validation after a platform build installs and launches successfully.

These files are scaffolds. During first-feature generation, replace placeholder text and actions with checks that match the generated feature contract.

Use `.opencode/guidance/templates/maestro-smoke-checklist-template.md` when replacing the placeholders.

Recommended use:

```bash
maestro test maestro/android/smoke.yaml
maestro test maestro/ios/smoke.yaml
```

If Maestro is not installed or a simulator/device is unavailable, record the check as `blocked by environment` in `.mvp/verification-report.md` instead of claiming validation passed.
