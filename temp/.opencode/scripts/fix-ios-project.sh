#!/usr/bin/env bash
# Re-applies the hand-maintained settings in iosApp/module.xcodeproj/project.pbxproj.
#
# The Kotlin Toolchain regenerates module.xcodeproj when it is missing and does not set a
# deployment target or the wrapper path we need. Run this after regenerating the project.
# It does NOT recreate the appTests unit-test target (see knowledge-graph file 37/38).
#
# Usage: .opencode/scripts/fix-ios-project.sh [bundle-id] [deployment-target]
#   bundle-id           default: keep the current value
#   deployment-target   default: 17.0
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
PBX="$ROOT/iosApp/module.xcodeproj/project.pbxproj"
BUNDLE_ID="${1:-}"
TARGET="${2:-17.0}"

[ -f "$PBX" ] || { echo "Missing $PBX" >&2; exit 1; }

# 1. The Toolchain integration script must stay a single string (xcodebuild -convert-project rewrites it).
if ! grep -q 'shellScript = "# !KOTLIN INTEGRATION STEP!' "$PBX"; then
  echo "ERROR: the 'Build Kotlin' shellScript is not the single-string form the Toolchain expects." >&2
  echo "Expected: shellScript = \"# !KOTLIN INTEGRATION STEP!\\n# This script is managed ...\\n...tool xcode-integration\\n\";" >&2
  exit 1
fi

# 1b. The Swift test action lives in iosApp.xcscheme (app.xcscheme has no test action). The Toolchain
#     can regenerate the project when iosApp sources change and, in doing so, strip the appTests
#     testable from that scheme. Re-insert it, using the appTests target id from the project file.
SCHEME="$ROOT/iosApp/module.xcodeproj/xcshareddata/xcschemes/iosApp.xcscheme"
[ -f "$SCHEME" ] || { echo "ERROR: missing $SCHEME (restore it from git or recreate the iosApp scheme)." >&2; exit 1; }
if ! grep -q 'BlueprintName = "appTests"' "$SCHEME"; then
  TESTS_ID="$(grep -oE '[0-9A-F]{24} /\* appTests \*/ = \{' "$PBX" | head -1 | cut -d' ' -f1)"
  [ -n "$TESTS_ID" ] || { echo "ERROR: no appTests target found in $PBX." >&2; exit 1; }
  TESTS_ID="$TESTS_ID" perl -0pi -e '
    my $id = $ENV{TESTS_ID};
    my $block = "      <Testables>\n         <TestableReference\n            skipped = \"NO\">\n            <BuildableReference\n               BuildableIdentifier = \"primary\"\n               BlueprintIdentifier = \"$id\"\n               BuildableName = \"appTests.xctest\"\n               BlueprintName = \"appTests\"\n               ReferencedContainer = \"container:module.xcodeproj\">\n            </BuildableReference>\n         </TestableReference>\n      </Testables>";
    s/[ ]*<Testables>\s*<\/Testables>/$block/ or die "no empty <Testables> element to fill\n";
  ' "$SCHEME"
  echo "Restored the appTests testable in iosApp.xcscheme (the Toolchain had removed it)."
fi

# 2. Wrapper path relative to the iosApp module root.
if grep -q 'KOTLIN_CLI_WRAPPER_PATH = ' "$PBX"; then
  sed -i '' 's#KOTLIN_CLI_WRAPPER_PATH = .*;#KOTLIN_CLI_WRAPPER_PATH = ../kotlin;#' "$PBX"
else
  echo "WARN: no KOTLIN_CLI_WRAPPER_PATH found; build once with ./kotlin build -m iosApp first." >&2
fi

# 3. Deployment target (the Toolchain leaves it unset, so apps only install on the newest iOS).
if grep -q 'IPHONEOS_DEPLOYMENT_TARGET' "$PBX"; then
  sed -i '' "s#IPHONEOS_DEPLOYMENT_TARGET = .*;#IPHONEOS_DEPLOYMENT_TARGET = ${TARGET};#" "$PBX"
else
  sed -i '' "s#^\(\t*\)SDKROOT = iphoneos;#\1SDKROOT = iphoneos;\n\1IPHONEOS_DEPLOYMENT_TARGET = ${TARGET};#" "$PBX"
fi

# 4. Optional bundle id for the app target (tests keep their own id).
if [ -n "$BUNDLE_ID" ]; then
  sed -i '' "s#PRODUCT_BUNDLE_IDENTIFIER = [^;]*\\.tests;#&#; /\\.tests;/! s#PRODUCT_BUNDLE_IDENTIFIER = .*;#PRODUCT_BUNDLE_IDENTIFIER = ${BUNDLE_ID};#" "$PBX"
fi

echo "Updated $PBX (deployment target ${TARGET}${BUNDLE_ID:+, bundle id ${BUNDLE_ID}})."
