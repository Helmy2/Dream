# 39. iOS String Localization Practices

The native SwiftUI host uses Apple's `.xcstrings` String Catalog for localization, while the Android-only Compose UI uses `shared/core-strings/composeResources/values/strings.xml`. These two sources must stay in sync.

### 39.1 Every shared string must match key and English value

For every user-visible string that appears in both the Compose and SwiftUI surfaces:

- The `.xcstrings` key must match the `strings.xml` `name` attribute
- The `.xcstrings` English value must match the `strings.xml` value
- Format specifiers: convert `%1$s` (XML) to `%@` (xcstrings)
- Adding a string to one file requires adding the same key and value to the other

Example — XML:
```xml
<string name="sample_title">Dream MVP Builder</string>
<string name="sample_occurred_row">Occurred: %1$s</string>
```

Example — matching xcstrings entries:
```json
"sample_title" : {
    "localizations" : {
        "en" : { "stringUnit" : { "state" : "translated", "value" : "Dream MVP Builder" } }
    }
},
"sample_occurred_row" : {
    "localizations" : {
        "en" : { "stringUnit" : { "state" : "translated", "value" : "Occurred: %@" } }
    }
}
```

> **See also:** [17. Resource And Localization Practices](17-resource-and-localization-practices.md) for the shared strings workflow and [31.5 All user-visible strings must come from shared resources](31-ui-quality-and-state-design-practices.md) for the Compose string resource pattern.

### 39.2 iOS-only strings belong only in xcstrings

Strings that are specific to the native SwiftUI host (instructional text, platform labels) do not need an XML counterpart. Document them with an `ios_` infix in the key:

- `sample_ios_host_description` — host architecture text
- `sample_ios_instruction` — template instantiation guidance
- `sample_occurred_label` — label used alongside formatted date
- `sample_category_label` — picker and section header label
- `sample_details_header` — form section header

### 39.3 Use `Text("key_name")` for localized lookup

In SwiftUI, pass the xcstrings key directly to `Text`:

```swift
Text("sample_title")
Text("sample_retry")
```

For TextField placeholders and Picker labels, the same pattern works because they also accept `LocalizedStringKey`.

For computed properties (like `SampleCategory.displayName`), use `String(localized:)`:

```swift
var displayName: String {
    switch self {
    case .contract: String(localized: "sample_category_contract")
    }
}
```

### 39.4 Verify both files during changes

When adding or changing a user-visible string:

1. Update `shared/core-strings/composeResources/values/strings.xml` first (source of truth for shared strings)
2. Add a matching entry to `iosApp/src/Localizable.xcstrings` with the same key and English value
3. If the string is iOS-only, add only to the xcstrings with an `ios_` infix
4. Verify both `./kotlin build -m iosApp --platform=iosSimulatorArm64` and `./kotlin build -m core-strings --platform=android` still pass

### 39.5 No hardcoded user-visible strings in SwiftUI views

All user-visible strings in the SwiftUI host must come from the xcstrings catalog. Hardcoded literal strings are only acceptable for:

- Accessibility labels and hints (which have their own `accessibilityLabel` modifier)
- Dynamic content that originates from Kotlin (loaded via the bridge)
- Debug-only text that is stripped from release builds

### 39.6 Avoid placeholder strings that clash with Form section headers

SwiftUI Form section headers are system-uppercased at runtime (e.g., `Text("Amount")` renders as "AMOUNT"). When a TextField placeholder value partially matches a section header, the header can shadow the placeholder in Maestro text selectors and in accessibility traversal.

Best practice:

- use placeholder values that are semantically distinct from any Form section header or label in the same screen (e.g., `"0.00"` instead of `"Amount"`)
- when a section header renders a category noun (Amount, Category, Date), the corresponding TextField or Picker placeholder should use a concrete example value rather than repeating the noun
- verify clashes by inspecting the accessibility tree with `mobile_list_elements_on_screen` (mobile-mcp) or by running Maestro against a test build

### 39.7 Verify xcstrings output with plutil after build

Xcode compiles `.xcstrings` into a binary `.plist` format during build. Syntax or key-reference errors are invisible at compile time and only surface at runtime as missing string lookups.

Best practice:

- after building the iOS target, locate the compiled `.plist` under the build products directory
- verify its structure with `plutil -p`:

```bash
plutil -p /path/to/build/Products/Debug-iphonesimulator/iosApp.app/Localizable.plist
```

- confirm expected keys are present with the correct English values
- a missing key at runtime is most likely an xcstrings format issue (trailing comma, missing stringUnit wrapper, or encoding) — `plutil` will surface structural errors before the app runs
- run `plutil -lint` on the source `.xcstrings` file as a pre-build sanity check:

```bash
plutil -lint iosApp/src/Localizable.xcstrings
```
