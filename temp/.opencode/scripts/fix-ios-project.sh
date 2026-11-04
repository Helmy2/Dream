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
