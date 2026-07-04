#!/bin/bash
export MOBILECLI_PATH=/opt/homebrew/bin/mobilecli
exec npx -y @mobilenext/mobile-mcp@latest "$@"
