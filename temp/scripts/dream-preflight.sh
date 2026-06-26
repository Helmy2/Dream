#!/usr/bin/env sh
set -u

status=0

pass() {
  printf 'passed: %s\n' "$1"
}

blocker() {
  printf 'blocked by environment: %s\n' "$1"
  status=1
}

if git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
  if git status --short >/dev/null 2>&1 && git diff --no-ext-diff --stat >/dev/null 2>&1; then
    if git log --oneline -10 >/dev/null 2>&1; then
      pass 'git repository and review commands are usable'
    else
      blocker 'git log --oneline -10 failed'
    fi
  else
    blocker 'git status or git diff failed'
  fi
else
  blocker 'project is not initialized as a git repository'
fi

sdk_path=""
if [ "${ANDROID_HOME:-}" != "" ]; then
  sdk_path="$ANDROID_HOME"
elif [ "${ANDROID_SDK_ROOT:-}" != "" ]; then
  sdk_path="$ANDROID_SDK_ROOT"
elif [ -f local.properties ]; then
  sdk_path=$(awk -F= '/^sdk.dir=/ { print $2 }' local.properties 2>/dev/null | sed 's#\\:#:#g')
fi

if [ "$sdk_path" != "" ] && [ -d "$sdk_path" ]; then
  pass "Android SDK found at $sdk_path"
  if [ ! -f local.properties ] || ! grep -q "^sdk.dir=$sdk_path" local.properties 2>/dev/null; then
    printf 'sdk.dir=%s\n' "$sdk_path" > local.properties
    pass "local.properties written with sdk.dir=$sdk_path"
  fi
else
  blocker 'Android SDK not found; set ANDROID_HOME, ANDROID_SDK_ROOT, or local.properties sdk.dir'
fi

if command -v maestro >/dev/null 2>&1; then
  pass 'Maestro is installed'
else
  blocker 'Maestro is not installed; install it before smoke validation or record this blocker'
fi

if command -v xcodebuild >/dev/null 2>&1; then
  pass 'Xcode is installed'
fi

if command -v java >/dev/null 2>&1; then
  pass 'Java is available'
else
  blocker 'Java is not available; install JDK 17+'
fi

exit "$status"
