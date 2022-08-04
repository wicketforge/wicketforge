<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# WicketForge Changelog
## [6.0.7]
### Added
- Changed version range to 221.* - 223.* otherwise IntelliJ 2022.2 marks the plugin as incompatible

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
