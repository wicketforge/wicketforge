<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# WicketForge Changelog

## [Unreleased]

## [6.0.10]
- Support for Intellij Idea 2024+
- switched from IntelliJ Idea to Intellij Platform

## [6.0.9]
- Support for IntelliJ IDEA 2023.3
- updated dependencies

## [6.0.8]
### Added
- Support for IntelliJ IDEA 2023.1

## [6.0.7]
### Changed
-  new minimum version is 2022.* (platform 222.*)
### Fixed
- InvalidArgumentException in MarkupWicketIdReferenceProvider
- upgraded deprecated functions to new API
### Added
- Support for 2022.3.*

## [6.0.6]
### Added
- Support for IntelliJ IDEA 2022.2

## [6.0.5]
### Added
- Support for IntelliJ IDEA 2022.1

## [6.0.4]
### Fixed
- IllegalStateException in WicketPsiUtils
- Wicket Icons appear as single black pixel


## [6.0.3]
### Fixed
- Wicket-ID References are now working reliable again

## [6.0.2]
### Fixed
- multiple PluginExceptions caused by missing Icons and Descriptions
### Changed
- removed deprecated ApplicationComponent-Classes and migrated to new API
- moved Icons from Constants to new class
### Added
- Updates to Plugin do no longer require IDE restart
- Added preliminary support for IntelliJ IDEA 2021.3
## [6.0.1]
### Fixed
- NoClassDefFoundError in PropertiesIndex.java

### Changed
- Removed unstable and deprecated API-Calls for IntelliJ IDEA 2021.3

## [6.0.0]
### Added
- Support for IntelliJ IDEA 2021.2

### Changed
- Changed build to Gradle Build System
