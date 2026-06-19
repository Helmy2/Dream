# 13. Time And Logical Date Practices

This architecture benefits from a particularly reusable time model.

### 12.1 One production clock access point

A dedicated production time provider should be the sole allowed caller of `Clock.System.now()`.

This should be copied directly into any app that depends on date-sensitive behavior.

### 12.2 Logical date is different from calendar date

The app supports the idea that the user's logical day may not reset at midnight.

Reusable principle:

- business time should be modeled explicitly when product rules depend on a special day boundary
