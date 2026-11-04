#!/usr/bin/env ruby
# frozen_string_literal: true

# OBSOLETE under the Kotlin Toolchain.
#
# iosApp/module.xcodeproj uses a file-system-synchronized folder over iosApp/src,
# so new Swift files are picked up by Xcode automatically. There is nothing to
# register. This stub is kept so older prompts that call it do not fail.

puts 'sync-pbxproj.rb is obsolete: Swift files in iosApp/src are picked up automatically.'
