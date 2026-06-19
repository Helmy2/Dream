# 22. Error Handling And Fallback Practices

Well-structured apps generally handle failure by degrading gracefully.

Examples:

- location failures fall back to Mecca coordinates in prayer-related logic
- invalid stored preference values fall back to safe defaults
- repository methods return typed failures instead of crashing normal app flow

Best practice:

- define safe defaults for non-fatal failures
- keep fallback behavior explicit and reviewable
- map infrastructure problems into business-level outcomes
