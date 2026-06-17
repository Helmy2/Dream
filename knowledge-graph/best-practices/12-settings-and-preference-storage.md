# 12. Settings And Preference Storage

Settings can be stored through DataStore Preferences and mapped into a domain model.

### 11.1 Observe one typed settings object

A settings repository can expose `Flow<AppSettings>` instead of many unrelated flows.

This is a strong practice because:

- consumers read one coherent settings snapshot
- defaults are centralized
- invalid stored enum values are safely recovered with fallback logic

### 11.2 Convert primitive storage into domain types at the repository boundary

Examples:

- strings into `CalculationMethod`
- strings into `AppTheme`
- strings into `AppLanguage`
- booleans and coordinates into `LocationMode`

Best practice:

- never let UI or use cases parse raw preference keys directly
