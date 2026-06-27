#!/usr/bin/env ruby
# frozen_string_literal: true

require 'xcodeproj'
require 'fileutils'

PROJECT_DIR = File.expand_path('..', __dir__)
PBXPROJ_PATH = File.join(PROJECT_DIR, 'iosApp-swiftui', 'iosApp.xcodeproj')
IOSAPP_DIR = File.join(PROJECT_DIR, 'iosApp-swiftui', 'iosApp')
TESTS_DIR = File.join(PROJECT_DIR, 'iosApp-swiftui', 'iosAppTests')

SKIP_PATTERNS = ['/build/', 'Preview Content'].freeze

def swift_files(dir)
  Dir.glob("#{dir}/**/*.swift")
     .reject { |f| SKIP_PATTERNS.any? { |pat| f.include?(pat) } }
     .map { |f| File.expand_path(f) }
     .sort
end

def relative_to_iosapp(path)
  path.sub("#{IOSAPP_DIR}/", '')
end

def test_file?(path)
  path.start_with?(TESTS_DIR)
end

def find_or_create_group(parent, segment)
  existing = parent.groups.find { |g| (g.name || g.path) == segment }
  return existing if existing

  parent.new_group(segment, segment)
end

def ensure_group_for_path(root_group, relative_path)
  segments = relative_path.split(File::SEPARATOR).reject(&:empty?)
  return root_group if segments.length <= 1

  current = root_group
  segments[0...-1].each do |seg|
    current = find_or_create_group(current, seg)
  end
  current
end

def file_already_registered?(project, real_path)
  project.files.any? { |f| f.real_path.to_s == real_path }
end

def register_new_files(project, app_target, test_target, iosapp_group, tests_group, app_files, test_files)
  all_files = app_files + test_files
  missing = all_files.reject { |f| file_already_registered?(project, f) }

  if missing.empty?
    return 0
  end

  puts "Found #{missing.size} unregistered Swift file(s):"
  missing.each { |f| puts "  #{relative_to_iosapp(f)}" }
  puts

  added = 0

  missing.each do |file|
    is_test = test_file?(file)
    basename = File.basename(file)

    target = is_test ? test_target : app_target
    group = if is_test
              tests_group || iosapp_group
            else
              rel = relative_to_iosapp(file)
              ensure_group_for_path(iosapp_group, rel)
            end

    begin
      ref = group.new_reference(basename)
      ref.path = basename
      ref.source_tree = '<group>'
      target.source_build_phase.add_file_reference(ref)
      puts "  Registered: #{basename} (group=#{group.name || group.path}, target=#{target.name})"
      added += 1
    rescue => e
      puts "  Warning: failed to register #{basename}: #{e.message}"
    end
  end

  added
end

def remove_deleted_files(project, app_target, test_target, iosapp_group, tests_group)
  # Collect all real paths that exist on disk
  existing_paths = {}
  (swift_files(IOSAPP_DIR) + swift_files(TESTS_DIR)).each do |f|
    existing_paths[File.expand_path(f)] = true
  end

  # Find file references for files that no longer exist
  targets = { 'iosApp' => app_target, 'iosAppTests' => test_target }
  removed = 0

  project.files.each do |file_ref|
    next unless file_ref.real_path
    real = file_ref.real_path.to_s
    next unless real.end_with?('.swift')
    next unless real.start_with?(IOSAPP_DIR) || real.start_with?(TESTS_DIR)

    if existing_paths[real]
      next
    end

    rel = relative_to_iosapp(real)

    # Remove PBXBuildFile entries from all target build phases
    targets.each_value do |target|
      next unless target
      target.source_build_phase.files.each do |build_file|
        next unless build_file.file_ref == file_ref

        target.source_build_phase.remove_build_file(build_file)
      end
    end

    # Remove the file reference from its parent group
    parent = file_ref.parent
    if parent
      parent.children.delete(file_ref)
    end

    puts "  Removed: #{rel}"
    removed += 1
  end

  removed
end

def run
  project = Xcodeproj::Project.open(PBXPROJ_PATH)

  app_target = project.targets.find { |t| t.name == 'iosApp' }
  test_target = project.targets.find { |t| t.name == 'iosAppTests' }

  unless app_target
    puts "Error: target 'iosApp' not found in project"
    return 1
  end

  iosapp_group = project.main_group.groups.find { |g| (g.name || g.path) == 'iosApp' }
  tests_group = project.main_group.groups.find { |g| (g.name || g.path) == 'iosAppTests' }

  unless iosapp_group
    puts "Error: group 'iosApp' not found in project"
    return 1
  end

  app_files = swift_files(IOSAPP_DIR)
  test_files = swift_files(TESTS_DIR)

  # Phase 1: remove deleted files from project
  puts "--- Checking for deleted files ---"
  removed = remove_deleted_files(project, app_target, test_target, iosapp_group, tests_group)
  if removed > 0
    puts "\nRemoved #{removed} deleted file reference(s).\n\n"
  else
    puts "No orphaned file references found.\n\n"
  end

  # Phase 2: register new files in project
  puts "--- Checking for unregistered files ---"
  added = register_new_files(project, app_target, test_target, iosapp_group, tests_group, app_files, test_files)
  if added == 0
    all_files = app_files + test_files
    puts "All #{all_files.size} Swift files are registered in the project."
  end

  needs_save = removed > 0 || added > 0

  if needs_save
    backup = "#{PBXPROJ_PATH}/project.pbxproj.bak"
    FileUtils.cp("#{PBXPROJ_PATH}/project.pbxproj", backup)
    puts "\nBackup saved to: #{backup}"

    project.save
    changes = []
    changes << "#{added} file(s) added" if added > 0
    changes << "#{removed} deleted reference(s) removed" if removed > 0
    puts "Project file updated (#{changes.join(', ')})."
  else
    puts "No changes needed."
  end

  # Final verification
  remaining_new = (app_files + test_files).reject { |f| file_already_registered?(project, f) }
  unless remaining_new.empty?
    puts "Warning: #{remaining_new.size} file(s) still not registered:"
    remaining_new.each { |f| puts "  #{relative_to_iosapp(f)}" }
  end

  0
end

exit run
