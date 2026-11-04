# 8. Shared Core Splitting

One of the best structural decisions in this style of architecture is splitting shared core infrastructure instead of making one giant `core` module.

Current split:

- `shared/core-domain`
- `shared/core-data`
- `shared/core-presentation`

Why this is worth reusing:

- pure modules do not accidentally pull in heavy runtime dependencies
- domain modules stay portable
- data modules only depend on the runtime tools they need
- presentation helpers are isolated from data and database code

Best rule:

- split shared core by responsibility and dependency weight, not just by name
