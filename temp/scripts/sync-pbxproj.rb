#!/usr/bin/env ruby
# frozen_string_literal: true

require 'securerandom'
require 'fileutils'
require 'set'

PBXPROJ = File.expand_path('../iosApp-swiftui/iosApp.xcodeproj/project.pbxproj', __dir__)
IOSAPP = File.expand_path('../iosApp-swiftui/iosApp', __dir__)
TESTS = File.expand_path('../iosApp-swiftui/iosAppTests', __dir__)

def generate_uuid
  SecureRandom.hex(12).upcase
end

def scan_swift_files(dir)
  Dir.glob("#{dir}/**/*.swift")
     .reject { |f| f.include?('/build/') || f.include?('Preview Content') }
     .map { |f| File.expand_path(f) }
     .sort
end

def relative_path(file, base)
  "/#{File.dirname(file).sub(base, '')}/#{File.basename(file)}"
     .sub(%r{^/}, '')
end

def file_group_path(file)
  dir = File.dirname(file)
  dir.sub(IOSAPP, '').sub(%r{^/}, '')
end

def in_test_target?(file)
  file.start_with?(TESTS)
end

# Parse the pbxproj into raw text sections
def parse_sections(text)
  sections = {}
  current_section = nil
  current_content = []

  text.each_line do |line|
    if line =~ /^\/\* Begin (.+?) section \*\/$/
      current_section = $1
      current_content = [line]
    elsif line =~ /^\/\* End (.+?) section \*\//
      current_content << line
      sections[current_section] = current_content.join
      current_section = nil
      current_content = []
    elsif current_section
      current_content << line
    end
  end

  sections
end

# Extract all PBXFileReference paths from the project
def existing_file_refs(text)
  refs = Set.new
  text.scan(%r{^\s+[A-F0-9]{24} /\* (.+?) \*/ = \{$}) do |_m|
    refs.add($1)
  end
  refs
end

# Extract all files already in PBXBuildFile section (by name)
def existing_build_files(text)
  files = Set.new
  text.scan(%r{/\* (.+?) in Sources \*/}) do |_m|
    files.add($1)
  end
  files
end

# Extract PBXGroup entries with their paths and children UUIDs
def parse_groups(text)
  groups = {}
  # Find each PBXGroup
  scanner = text.dup
  while scanner =~ /\n\t\t([A-F0-9]{24}) \/\* (.+?) \*\/ = \{\n\t\t\tisa = PBXGroup;\n\t\t\tchildren = \((.*?)\);\n\t\t\tpath = (.+?);\n\t\t\tsourceTree = "<group>";\n\t\t\};/m
    uuid = $1
    name = $2
    children_list = $3
    path = $4
    children = children_list.scan(/[A-F0-9]{24}/)
    groups[uuid] = { name: name, path: path, children: children }
    scanner = $~.post_match
  end
  groups
end

# Find which group UUID contains a given path
def find_group_for_path(groups, target_path, all_refs_by_path)
  # Walk the group tree to find the group whose path matches the directory
  segments = target_path.split('/')
  root_uuid = nil

  # Find root group (the one with path = "iosApp" or "iosAppTests")
  groups.each do |uuid, info|
    if info[:path] == 'iosApp' || info[:path] == 'iosAppTests'
      root_uuid = uuid
      break
    end
  end

  return nil, nil unless root_uuid

  current_uuid = root_uuid
  matched_path = ''

  segments.each do |seg|
    next if seg.empty?
    found = false
    groups.each do |uuid, info|
      next unless info[:path] == seg

      # Check if this group is a child of current_uuid
      if groups[current_uuid] && groups[current_uuid][:children].include?(uuid)
        current_uuid = uuid
        matched_path = matched_path.empty? ? seg : "#{matched_path}/#{seg}"
        found = true
        break
      end
    end
    break unless found
  end

  [current_uuid, matched_path]
end

def run
  proj_text = File.read(PBXPROJ)
  existing_refs = existing_file_refs(proj_text)
  existing_build = existing_build_files(proj_text)

  app_files = scan_swift_files(IOSAPP)
  test_files = scan_swift_files(TESTS)
  all_files = app_files + test_files

  missing = all_files.reject do |f|
    basename = File.basename(f)
    existing_refs.include?(basename) || existing_build.include?(basename)
  end

  if missing.empty?
    puts "All #{all_files.size} Swift files are registered in the project."
    return 0
  end

  puts "Found #{missing.size} unregistered Swift file(s):"
  missing.each { |f| puts "  #{relative_path(f, IOSAPP)}" }
  puts

  missing.each do |file|
    basename = File.basename(file)
    file_ref_uuid = generate_uuid
    build_file_uuid = generate_uuid
    group_path = file_group_path(file)
    is_test = in_test_target?(file)

    # PBXFileReference entry
    file_ref_entry = "\t\t#{file_ref_uuid} /* #{basename} */ = {isa = PBXFileReference; lastKnownFileType = sourcecode.swift; path = #{basename}; sourceTree = \"<group>\"; };\n"

    # Insert after the last PBXFileReference entry
    proj_text.sub!(/(\n\/\* End PBXFileReference section \*\/)/, "#{file_ref_entry}\\1")

    # PBXGroup entry: find the right group and add this file's ref
    if is_test
      # Add to iosAppTests group
      proj_text.sub!(/(\t\tA30000000000000000000001 \/\* iosAppTests \*\/ = \{\n\t\t\tisa = PBXGroup;\n\t\t\tchildren = \()/) do |match|
        "#{match}\n\t\t\t\t#{file_ref_uuid} /* #{basename} */,"
      end
    elsif group_path.empty?
      # Add to iosApp root group
      proj_text.sub!(/(\t\t7555FF7D242A565900829871 \/\* iosApp \*\/ = \{\n\t\t\tisa = PBXGroup;\n\t\t\tchildren = \()/) do |match|
        "#{match}\n\t\t\t\t#{file_ref_uuid} /* #{basename} */,"
      end
    else
      # Find the matching subgroup and add there
      group_pattern = nil
      proj_text.scan(/(\t\t([A-F0-9]{24}) \/\* (.+?) \*\/ = \{\n\t\t\tisa = PBXGroup;\n\t\t\tchildren = \((.*?)\);\n\t\t\tpath = #{Regexp.escape(group_path.split('/').last)};)/m) do |_m|
        group_pattern = $1
      end

      if group_pattern
        # Found the group, add to its children
        proj_text.sub!(/(\t\t#{group_pattern} \/\* #{Regexp.escape(group_path.split('/').last)} \*\/ = \{\n\t\t\tisa = PBXGroup;\n\t\t\tchildren = \()/) do |match|
          "#{match}\n\t\t\t\t#{file_ref_uuid} /* #{basename} */,"
        end
      else
        puts "  Warning: Could not find group for #{group_path}, adding to iosApp root group"
        proj_text.sub!(/(\t\t7555FF7D242A565900829871 \/\* iosApp \*\/ = \{\n\t\t\tisa = PBXGroup;\n\t\t\tchildren = \()/) do |match|
          "#{match}\n\t\t\t\t#{file_ref_uuid} /* #{basename} */,"
        end
      end
    end

    # PBXBuildFile entry
    target = is_test ? 'Sources' : 'Sources'
    build_entry = "\t\t#{build_file_uuid} /* #{basename} in #{target} */ = {isa = PBXBuildFile; fileRef = #{file_ref_uuid} /* #{basename} */; };\n"
    proj_text.sub!(/(\n\/\* End PBXBuildFile section \*\/)/, "#{build_entry}\\1")

    # PBXSourcesBuildPhase entry
    phase_uuid = is_test ? 'A50000000000000000000001' : '7555FF77242A565900829871'
    phase_entry = "\t\t\t\t#{build_file_uuid} /* #{basename} in #{target} */,\n"
    proj_text.sub!(/(#{phase_uuid} \/\* Sources \*\/ = \{\n\t\t\tisa = PBXSourcesBuildPhase;\n\t\t\tbuildActionMask = 2147483647;\n\t\t\tfiles = \()/) do |match|
      "#{match}\n#{phase_entry}"
    end

    puts "  Registered: #{basename} (ref=#{file_ref_uuid}, build=#{build_file_uuid})"
  end

  # Write back
  backup = "#{PBXPROJ}.bak"
  FileUtils.cp(PBXPROJ, backup)
  puts "\nBackup saved to: #{backup}"
  File.write(PBXPROJ, proj_text)
  puts "Project file updated."

  existing_refs = existing_file_refs(File.read(PBXPROJ))
  existing_build = existing_build_files(File.read(PBXPROJ))
  all_files = scan_swift_files(IOSAPP) + scan_swift_files(TESTS)
  still_missing = all_files.reject { |f| existing_refs.include?(File.basename(f)) || existing_build.include?(File.basename(f)) }

  if still_missing.empty?
    puts "All files are now registered. ✓"
  else
    puts "Warning: #{still_missing.size} file(s) still not registered:"
    still_missing.each { |f| puts "  #{relative_path(f, IOSAPP)}" }
  end

  0
end

exit run
