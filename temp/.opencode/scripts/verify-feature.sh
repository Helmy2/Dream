#!/usr/bin/env bash
# Verifies that a feature follows Dream's structure and, unless --quick is given, that the project
# builds and its tests pass. Read-only apart from build output and the idempotent iOS scheme repair.
#
# Usage: .opencode/scripts/verify-feature.sh <feature-name> [options]
#   <feature-name>     lowercase, hyphenated, e.g. `expense` or `expense-details`
#   --quick            structural checks only (seconds); skips builds and tests
#   --no-ios           treat the feature as Android-only (skips iOS checks)
#   --ios-dir <Dir>    SwiftUI folder under iosApp/src (default: PascalCase of the feature name)
#   --simulator <name> iOS simulator name for Swift tests (default: first available iPhone)
#   -h, --help
#
# Output: one line per check: passed / FAILED / warning / not applicable / not run.
# Exit code: 0 when nothing FAILED and, in a full run, nothing was left "not run"; 1 otherwise.
# Warnings never fail the run; read them anyway. --quick always reports B1 as not run and can still exit 0.
#
# Needs bash, grep, find, sed, awk and python3 (for Localizable.xcstrings). Works with macOS bash 3.2.

set -u

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
cd "$ROOT" || exit 2

usage() { sed -n '2,19p' "$0" | sed 's/^# \{0,1\}//'; exit "${1:-2}"; }

FEATURE=""; QUICK=false; IOS=auto; IOS_DIR=""; SIM=""
while [ $# -gt 0 ]; do
  case "$1" in
    --quick) QUICK=true; shift ;;
    --no-ios) IOS=no; shift ;;
    --ios-dir) [ $# -ge 2 ] || usage; IOS_DIR="$2"; shift 2 ;;
    --simulator) [ $# -ge 2 ] || usage; SIM="$2"; shift 2 ;;
    -h|--help) usage 0 ;;
    -*) echo "Unknown option: $1" >&2; usage ;;
    *) [ -z "$FEATURE" ] || usage; FEATURE="$1"; shift ;;
  esac
done
[ -n "$FEATURE" ] || usage
if ! printf '%s' "$FEATURE" | grep -Eq '^[a-z][a-z0-9]*(-[a-z0-9]+)*$'; then
  echo "Invalid feature name '$FEATURE': use lowercase words separated by hyphens (e.g. expense-details)." >&2
  exit 2
fi

F="$FEATURE"
PREFIX="$(printf '%s' "$F" | tr '-' '_')_"
if [ -z "$IOS_DIR" ]; then
  IFS='-' read -r -a _parts <<< "$F"
  for _p in "${_parts[@]}"; do IOS_DIR="${IOS_DIR}$(printf '%s' "${_p:0:1}" | tr '[:lower:]' '[:upper:]')${_p:1}"; done
fi

PASSED=0; FAILED=0; WARNED=0; NA=0; NOTRUN=0
emit() { # status id message
  case "$1" in
    ok)   PASSED=$((PASSED + 1)); printf 'passed          %-5s %s\n' "$2" "$3" ;;
    fail) FAILED=$((FAILED + 1)); printf 'FAILED          %-5s %s\n' "$2" "$3" ;;
    warn) WARNED=$((WARNED + 1)); printf 'warning         %-5s %s\n' "$2" "$3" ;;
    na)   NA=$((NA + 1));         printf 'not applicable  %-5s %s\n' "$2" "$3" ;;
    nr)   NOTRUN=$((NOTRUN + 1)); printf 'not run         %-5s %s\n' "$2" "$3" ;;
  esac
}

FIX="./.opencode/scripts/fix-ios-project.sh"
# The Toolchain can strip the appTests testable from iosApp.xcscheme when it regenerates the Xcode
# project. fix-ios-project.sh restores it and is idempotent, so run it before anything needs Swift tests.
repair_scheme() { # id
  if [ -x "$FIX" ] && [ -d iosApp/module.xcodeproj ]; then
    local out rc
    out="$("$FIX" 2>&1)"; rc=$?
    if [ $rc -ne 0 ]; then emit fail "$1" "fix-ios-project.sh failed: $(echo "$out" | head -2 | tr '\n' ' ')"
    elif echo "$out" | grep -q 'Restored the appTests testable'; then emit warn "$1" "the Toolchain had stripped the appTests testable from iosApp.xcscheme; restored it (do not commit the stripped scheme)"
    else emit ok "$1" "iosApp.xcscheme still has the appTests testable"; fi
  fi
}

LAYERS="domain data api impl"
mod_dir() { printf 'feature/%s-%s' "$F" "$1"; }
has_mod() { [ -f "$(mod_dir "$1")/module.yaml" ]; }
src_dirs() { # existing source dirs of a layer module (common + platform variants)
  for d in "$(mod_dir "$1")"/src "$(mod_dir "$1")"/src@*; do [ -d "$d" ] && printf '%s\n' "$d"; done
}
grep_src() { # layer, extended-regex -> matching lines (file:line:text)
  local d; for d in $(src_dirs "$1"); do grep -rnE "$2" "$d" --include='*.kt' 2>/dev/null; done
}
# non-comment dependency lines of a module.yaml
deps_of() { grep -E '^[[:space:]]*-[[:space:]]' "$(mod_dir "$1")/module.yaml" 2>/dev/null || true; }

printf 'Verifying feature "%s" in %s\n\n' "$F" "$ROOT"

# ---------------------------------------------------------------- M: modules
for l in domain api impl; do
  if has_mod "$l"; then emit ok M1 "module $(mod_dir "$l") exists with module.yaml"
  else emit fail M1 "missing $(mod_dir "$l")/module.yaml"; fi
done
if has_mod data; then emit ok M1 "module $(mod_dir data) exists with module.yaml"
else emit na M1 "no $(mod_dir data) (data module is optional: only needed for persistence or network)"; fi

if ! has_mod domain && ! has_mod api && ! has_mod impl; then
  printf '\nNo modules found for feature "%s". Create the modules first.\n' "$F"
  printf '\nSummary: %d passed, %d FAILED, %d warning, %d not applicable, %d not run\n' "$PASSED" "$FAILED" "$WARNED" "$NA" "$NOTRUN"
  exit 1
fi

for l in $LAYERS; do
  has_mod "$l" || continue
  if grep -Eq "^[[:space:]]*-[[:space:]]+$(mod_dir "$l")[[:space:]]*$" project.yaml; then emit ok M2 "$(mod_dir "$l") is listed in project.yaml"
  else emit fail M2 "$(mod_dir "$l") is not listed in project.yaml"; fi
done

dups="$(grep -E '^[[:space:]]*-[[:space:]]+[A-Za-z0-9_/-]+[[:space:]]*$' project.yaml | sed -E 's/^[[:space:]]*-[[:space:]]+//; s/[[:space:]]+$//' | awk -F/ '{print $NF}' | sort | uniq -d)"
if [ -z "$dups" ]; then emit ok M3 "module names in project.yaml are unique"
else emit fail M3 "duplicate module names (they must equal unique directory names): $(echo $dups)"; fi

# ---------------------------------------------------------------- K: Koin moduleId
bad=""
for l in $LAYERS; do
  has_mod "$l" || continue
  y="$(mod_dir "$l")/module.yaml"
  if grep -q 'io.insert-koin.compiler.plugin' "$y"; then
    id="$(grep -E '^[[:space:]]*moduleId:' "$y" | head -1 | sed -E 's/^[[:space:]]*moduleId:[[:space:]]*//; s/[[:space:]]+$//')"
    [ "$id" = "$F-$l" ] || bad="$bad $l(moduleId='${id:-none}', expected '$F-$l')"
  fi
done
if [ -z "$bad" ]; then emit ok K1 "Koin compiler plugin moduleId equals the module name wherever the plugin is applied"
else emit fail K1 "wrong Koin moduleId:$bad"; fi

ids="$(grep -rhE --include=module.yaml --exclude-dir=build --exclude-dir=.kotlin --exclude-dir=.opencode '^[[:space:]]*moduleId:' . 2>/dev/null | sed -E 's/^[[:space:]]*moduleId:[[:space:]]*//' | sort | uniq -d)"
if [ -z "$ids" ]; then emit ok K2 "Koin moduleIds are unique across the project"
else emit fail K2 "duplicate Koin moduleId: $(echo $ids)"; fi

# ---------------------------------------------------------------- D: layer rules
if has_mod domain; then
  y="$(mod_dir domain)/module.yaml"
  v="$(deps_of domain | grep -Ei 'compose|androidx\.room|ktor|koin|//feature/.*-(data|impl|api)|//shared/(core-data|designsystem|navigation|core-presentation|umbrella)' || true)"
  imp="$(grep_src domain '^import (android\.|androidx\.|io\.ktor|org\.koin|org\.jetbrains\.compose)' | head -3)"
  if [ -z "$v" ] && [ -z "$imp" ]; then emit ok D1 "domain is pure Kotlin (no UI, persistence, network or DI dependencies)"
  else emit fail D1 "domain is not pure Kotlin: $(printf '%s %s' "$(echo $v)" "$(echo $imp | cut -c1-160)")"; fi
fi

if has_mod api; then
  v="$(deps_of api | grep -Ei '//feature/|compose|koin|room' || true)"
  koin="$(grep_src api 'org\.koin' | head -2)"
  if [ -z "$v" ] && [ -z "$koin" ]; then emit ok D2 "api has no feature, UI or Koin dependencies"
  else emit fail D2 "api must stay a minimal contract: $(echo $v $koin | cut -c1-160)"; fi
  if grep_src api 'NavKey' | grep -q .; then emit ok D3 "api declares a NavKey route type"
  else emit fail D3 "no NavKey route type found in $(mod_dir api)/src"; fi
fi

if has_mod data; then
  v="$(deps_of data | grep -Ei "//feature/$F-impl|core-presentation|designsystem|navigation|compose" || true)"
  if [ -z "$v" ]; then emit ok D4 "data does not depend on presentation, design system, navigation or impl"
  else emit fail D4 "data depends on presentation-layer modules: $(echo $v)"; fi
fi

if has_mod impl; then
  v=""
  deps_of impl | grep -q "//feature/$F-data" && v="impl depends on its own -data module (use domain interfaces)"
  deps_of impl | grep -q "//feature/$F-api" || v="$v; impl does not depend on its own -api"
  if [ -z "$v" ]; then emit ok D5 "impl depends on api and domain, not on data"
  else emit fail D5 "$v"; fi
fi

others="$(grep -rlE --include=module.yaml --exclude-dir=build --exclude-dir=.kotlin --exclude-dir=.opencode "//feature/$F-impl" . 2>/dev/null | grep -v "^./feature/$F-" | grep -v '^./shared/umbrella-ui/' || true)"
if [ -z "$others" ]; then emit ok X1 "only umbrella-ui and the feature itself depend on $F-impl"
else emit fail X1 "other modules depend on $F-impl (use $F-api): $(echo $others)"; fi

other_data="$(grep -rlE --include=module.yaml --exclude-dir=build --exclude-dir=.kotlin --exclude-dir=.opencode "//feature/$F-data" . 2>/dev/null | grep -v "^./feature/$F-" | grep -v '^./shared/umbrella-' || true)"
if [ -z "$other_data" ]; then emit ok X2 "no other feature or shell module depends on $F-data"
else emit warn X2 "cross-feature data reuse is an explicit MVP exception; document it: $(echo $other_data)"; fi

# ---------------------------------------------------------------- W: wiring
UMB_UI="shared/umbrella-ui/module.yaml"; UMB_CORE="shared/umbrella-core/module.yaml"
miss=""
has_mod impl && { grep -Eq "//feature/$F-impl" "$UMB_UI" 2>/dev/null || miss="$miss $F-impl"; }
has_mod api  && { grep -Eq "//feature/$F-api"  "$UMB_UI" 2>/dev/null || miss="$miss $F-api"; }
if [ -z "$miss" ]; then emit ok W1 "umbrella-ui depends on the feature's impl and api modules"
else emit fail W1 "umbrella-ui/module.yaml is missing:$miss"; fi

names=""
has_mod impl && names="$(grep_src impl '^val [A-Za-z0-9_]+ *= *module' | sed -E 's/.*val ([A-Za-z0-9_]+).*/\1/')"
if has_mod impl; then
  if [ -z "$names" ]; then emit fail W2 "no top-level Koin module (val xxxUiModule = module { ... }) in $(mod_dir impl)"
  else
    miss=""; for n in $names; do grep -rqw "$n" shared/umbrella-ui/src 2>/dev/null || miss="$miss $n"; done
    if [ -z "$miss" ]; then emit ok W2 "impl Koin module(s) are installed in umbrella-ui: $(echo $names)"
    else emit fail W2 "impl Koin module(s) not referenced in shared/umbrella-ui/src:$miss"; fi
  fi
  if grep_src impl 'navigation<' | grep -q .; then emit ok W3 "impl installs Navigation 3 entry providers (navigation<Route> { ... })"
  else emit fail W3 "no navigation<Route> entry provider found in $(mod_dir impl)"; fi
fi

if has_mod data; then
  fns="$(grep_src data '^fun [A-Za-z0-9_]+Module\(' | sed -E 's/.*fun ([A-Za-z0-9_]+)\(.*/\1/')"
  if [ -z "$fns" ]; then emit na W4 "$F-data declares no *Module() function to install"
  else
    miss=""; for n in $fns; do grep -rqw "$n" shared/umbrella-ui/src shared/umbrella-core/src 2>/dev/null || miss="$miss $n"; done
    if [ -z "$miss" ]; then emit ok W4 "data Koin module(s) are installed: $(echo $fns)"
    else emit fail W4 "data Koin module(s) not referenced in umbrella-ui/umbrella-core:$miss"; fi
  fi
fi

# ---------------------------------------------------------------- T: tests
for l in domain data impl; do
  has_mod "$l" || continue
  n="$(find "$(mod_dir "$l")/test" -name '*Test.kt' 2>/dev/null | wc -l | tr -d ' ')"
  if [ "${n:-0}" -gt 0 ]; then emit ok T1 "$(mod_dir "$l") has $n test file(s)"
  else emit fail T1 "$(mod_dir "$l") has no *Test.kt under test/"; fi
done

# ---------------------------------------------------------------- S: strings and design system
if has_mod impl; then
  lit="$(grep_src impl '\bText\([[:space:]]*(text[[:space:]]*=[[:space:]]*)?"' | head -5)"
  if [ -z "$lit" ]; then emit ok S1 "impl has no hardcoded Text(\"...\") literals"
  else emit warn S1 "hardcoded user-visible text (use shared/core-strings): $(echo "$lit" | head -2 | cut -c1-140 | tr '\n' ' ')"; fi
  raw="$(for d in $(src_dirs impl); do grep -rlE 'import (androidx|org\.jetbrains)\.compose\.material3' "$d" --include='*.kt' 2>/dev/null; done)"
  if [ -z "$raw" ]; then emit ok S2 "impl uses design-system components, no raw Material3 imports"
  else emit warn S2 "raw Material3 imports (document the exception or add a design-system wrapper): $(echo "$raw" | sed 's|.*/||' | tr '\n' ' ')"; fi
fi

ANDROID_STRINGS="shared/core-strings/composeResources/values/strings.xml"
XCSTRINGS="iosApp/src/Localizable.xcstrings"
akeys="$( [ -f "$ANDROID_STRINGS" ] && grep -oE "name=\"${PREFIX}[A-Za-z0-9_]*\"" "$ANDROID_STRINGS" | sed -E 's/name="(.*)"/\1/' | sort -u )"
if [ -n "$akeys" ]; then emit ok S3 "$(echo "$akeys" | wc -l | tr -d ' ') Android string key(s) with prefix ${PREFIX}"
else emit warn S3 "no Android string keys with prefix ${PREFIX} in $ANDROID_STRINGS (use a different prefix? then S4 cannot compare)"; fi

# ---------------------------------------------------------------- I: iOS
ios_present=false
[ "$IOS" != no ] && [ -d "iosApp/src/$IOS_DIR" ] && ios_present=true

if [ "$IOS" = no ]; then
  emit na I1 "--no-ios: iOS checks skipped"
elif ! $ios_present; then
  emit na I1 "no iosApp/src/$IOS_DIR (pass --ios-dir <Dir> if the folder is named differently, or --no-ios)"
else
  miss=""; for sub in Views ViewModels; do [ -d "iosApp/src/$IOS_DIR/$sub" ] || miss="$miss $sub"; done
  if [ -z "$miss" ]; then emit ok I1 "iosApp/src/$IOS_DIR has Views and ViewModels"
  else emit fail I1 "iosApp/src/$IOS_DIR is missing:$miss"; fi

  if has_mod data; then
    if [ -d "iosApp/src/$IOS_DIR/Bridge" ] && find "iosApp/src/$IOS_DIR/Bridge" -name '*.swift' | grep -q .; then
      emit ok I2 "iOS bridge present in iosApp/src/$IOS_DIR/Bridge"
    else emit fail I2 "no Swift bridge in iosApp/src/$IOS_DIR/Bridge (the feature has a -data module)"; fi
  fi

  repair_scheme I4

  tests="$(grep -rlE "$IOS_DIR|${F}" iosApp/test --include='*.swift' 2>/dev/null | wc -l | tr -d ' ')"
  if [ "${tests:-0}" -gt 0 ]; then emit ok I3 "$tests Swift test file(s) in iosApp/test reference the feature"
  else emit fail I3 "no Swift tests in iosApp/test reference $IOS_DIR"; fi

  if [ -f "$XCSTRINGS" ] && [ -f "$ANDROID_STRINGS" ] && command -v python3 >/dev/null 2>&1; then
    # Enforces best-practices/39: every Android key exists on iOS, iOS-only keys use an ios_ infix, and the
    # English values match once %1$s is converted to %@.
    s4="$(python3 -I - "$ANDROID_STRINGS" "$XCSTRINGS" "$PREFIX" <<'PY'
import json, re, sys
import xml.etree.ElementTree as ET

xml_path, xc_path, prefix = sys.argv[1:4]
esc = {"n": "\n", "t": "\t"}
unescape = lambda v: re.sub(r"\\(.)", lambda m: esc.get(m.group(1), m.group(1)), v)
android = {e.get("name"): unescape(e.text or "") for e in ET.parse(xml_path).getroot().findall("string")
           if e.get("name", "").startswith(prefix)}
ios = {}
for k, v in json.load(open(xc_path, encoding="utf-8")).get("strings", {}).items():
    if k.startswith(prefix):
        unit = v.get("localizations", {}).get("en", {}).get("stringUnit")
        ios[k] = unit["value"] if unit else None
convert = lambda v: re.sub(r"%\d+\$s", "%@", v)
only_a = sorted(set(android) - set(ios))
only_i = sorted(set(ios) - set(android))
no_infix = [k for k in only_i if "_ios_" not in k]
mismatch = [k for k in sorted(set(android) & set(ios)) if ios[k] is not None and convert(android[k]) != ios[k]]
problems = []
if only_a: problems.append("Android keys missing on iOS: " + " ".join(only_a))
if no_infix: problems.append("iOS-only keys without an ios_ infix (rename to <prefix>ios_...): " + " ".join(no_infix))
if mismatch: problems.append("English values differ (after %1$s -> %@): " + " ".join(mismatch))
if problems:
    print("FAIL|" + " | ".join(problems))
else:
    extra = f" ({len(only_i)} iOS-only key(s) with ios_ infix)" if only_i else ""
    print(f"OK|Android and iOS strings match for prefix {prefix}: {len(android)} key(s), values in sync{extra}")
PY
)"
    case "$s4" in
      FAIL\|*) emit fail S4 "${s4#FAIL|}" ;;
      OK\|*)   emit ok S4 "${s4#OK|}" ;;
      *)       emit nr S4 "string parity check produced no result" ;;
    esac
  else
    emit nr S4 "string parity not checked (needs $ANDROID_STRINGS, $XCSTRINGS and python3)"
  fi
fi

# ---------------------------------------------------------------- E: Maestro
MA="maestro/features/$F"
if [ -f "$MA/android.yaml" ]; then
  appid="$(grep -E '^applicationId:|^[[:space:]]*applicationId:' androidApp/module.yaml | head -1 | sed -E 's/.*applicationId:[[:space:]]*//; s/[[:space:]]+$//')"
  if [ -n "$appid" ] && grep -q "appId: $appid" "$MA/android.yaml"; then emit ok E1 "$MA/android.yaml exists with appId $appid"
  else emit fail E1 "$MA/android.yaml appId does not match androidApp applicationId '${appid:-unknown}'"; fi
else emit fail E1 "missing $MA/android.yaml"; fi
if $ios_present; then
  if [ -f "$MA/ios.yaml" ]; then emit ok E2 "$MA/ios.yaml exists"
  else emit fail E2 "missing $MA/ios.yaml (the feature has an iOS screen)"; fi
fi

# ---------------------------------------------------------------- B: toolchain, build, tests
LOGDIR="$ROOT/build/verify-feature"
step() { # id description command...
  local id="$1" desc="$2"; shift 2
  mkdir -p "$LOGDIR"; local log="$LOGDIR/$id.log"
  if "$@" > "$log" 2>&1; then emit ok "$id" "$desc"
  else emit fail "$id" "$desc (see $log)"; return 1; fi
}

if $QUICK; then
  emit nr B1 "--quick: ./kotlin build, tests, app builds and Swift tests were not run"
else
  step B1 "./kotlin build" ./kotlin build
  step B2 "./kotlin test --platform=android" ./kotlin test --platform=android
  step B3 "./kotlin test --platform=iosSimulatorArm64" ./kotlin test --platform=iosSimulatorArm64
  step B4 "./kotlin build -m androidApp --platform=android" ./kotlin build -m androidApp --platform=android
  if $ios_present; then
    step B5 "./kotlin build -m iosApp --platform=iosSimulatorArm64" ./kotlin build -m iosApp --platform=iosSimulatorArm64
    repair_scheme B6
    if [ -z "$SIM" ]; then
      SIM="$(xcrun simctl list devices available 2>/dev/null | grep -E '^[[:space:]]+iPhone' | head -1 | sed -E 's/^[[:space:]]+//; s/ \([0-9A-F-]{36}\).*//')"
    fi
    PHYSICAL="$(pwd -P)"
    case "$PHYSICAL" in
      /private/*)
        # /tmp, /var and /etc are symlinks into /private. Xcode reports the symlinked form (SRCROOT=/tmp/...)
        # and the Kotlin Toolchain's xcode-integration cannot match it ("Unable to resolve a module with the
        # module directory"), so the Swift tests cannot run from such a location.
        emit nr B7 "Swift tests not run: $PHYSICAL is under a symlinked macOS path (/tmp, /var, /etc). The Toolchain's Xcode integration cannot resolve it; move the project elsewhere (for example under \$HOME) and run again." ;;
      *)
    if [ -z "$SIM" ]; then emit nr B7 "Swift tests: no available iPhone simulator found (pass --simulator <name>)"
    else
      step B7 "Swift tests on '$SIM' (xcodebuild test -scheme iosApp)" \
        xcodebuild test -project iosApp/module.xcodeproj -scheme iosApp -destination "platform=iOS Simulator,name=$SIM" \
          -derivedDataPath "$LOGDIR/derived-data" CODE_SIGNING_ALLOWED=NO
    fi
        ;;
    esac
  else
    emit na B5 "iOS build and Swift tests skipped (no iOS screen for this feature)"
  fi
fi

printf '\nSummary: %d passed, %d FAILED, %d warning, %d not applicable, %d not run\n' "$PASSED" "$FAILED" "$WARNED" "$NA" "$NOTRUN"
if [ "$FAILED" -gt 0 ]; then
  echo "Result: NOT READY. Fix every FAILED line, then run again."
  exit 1
fi
if $QUICK; then echo "Result: structure OK. Run without --quick before reporting success."; exit 0; fi
if [ "$NOTRUN" -gt 0 ]; then
  echo "Result: INCOMPLETE. $NOTRUN check(s) did not run (see the 'not run' lines). Do not report success until they run."
  exit 1
fi
echo "Result: structure, build and tests OK (warnings still need a look; device launch and Maestro are separate steps)."
exit 0
