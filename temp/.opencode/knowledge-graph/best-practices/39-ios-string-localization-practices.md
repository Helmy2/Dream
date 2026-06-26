# 39. iOS String Localization Practices

The native SwiftUI host uses Apple's `.xcstrings` String Catalog for localization, while the shared Compose Multiplatform UI uses `shared/core/strings/strings.xml`. These two sources must stay in sync.

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

1. Update `shared/core/strings/strings.xml` first (source of truth for shared strings)
2. Add a matching entry to `iosApp-swiftui/iosApp/Localizable.xcstrings` with the same key and English value
3. If the string is iOS-only, add only to the xcstrings with an `ios_` infix
4. Verify both `xcodebuild build` and `./gradlew :shared:core:strings:allTests` still pass

### 39.5 No hardcoded user-visible strings in SwiftUI views

All user-visible strings in the SwiftUI host must come from the xcstrings catalog. Hardcoded literal strings are only acceptable for:

- Accessibility labels and hints (which have their own `accessibilityLabel` modifier)
- Dynamic content that originates from Kotlin (loaded via the bridge)
- Debug-only text that is stripped from release builds
