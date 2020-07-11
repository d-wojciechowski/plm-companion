# Changelog

## [0.9.0]
### Changed
- Introduced better [changelog format](https://github.com/JetBrains/gradle-changelog-plugin)

## [0.8.0]
### Added
- Rerun button on command pane, it allows to quickly rerun the selected command.
- MS/BMS/Custom log pane supports line wrapping
### Changed
- Changed connection method with the addon, added retry mechanism, calls are now non-blocking
- Command log panel is now initialized early, so that commands are visible on every call

## [0.7.1]
### Added
- Option to wait until the finish of remote command
- Option to immediately switch to command pane

### Fixed
- Issue with linking run configurations.
- Shared command between run configurations.
- Alias not being saved

## [0.7.0]
### Added
- Button group to navigation bar with an option to disable it in the configuration.
- Combobox to LoadFromFile dialog which presents all run configurations, if one is selected it will be run before loading from file.
- "Remote command run" configuration, which allows other run configurations to be run before.
- Option to control actions such as wcstop/wcstart... to be controlled via System status.
- If addon is not available, easier to read message is shown.
### Changed
- Modified settings dialog to be easier to read.

## [0.6.1]
### Added
- Config for the port on which communication will be running with addon.
### Fixed
- Saving execution time for commands.

## [0.6.0]
### Added
- Load from file action under right click menu in project pane.
- Support for Intellij 2020.1

## [0.5.0]
### Changed
- Communication library changed from GRPC to RSocket.

## [0.4.0]
### Added
- Custom command execution
- Custom file remote tailing
- Remote file picker
### Changed
- Communication library changed from GRPC to RSocket.
- Icons from Intellij IDEA now being used, to unify look with OOTB.

## [0.3.2]
### Added
- Compatibility with 2019.3

## [0.3.0]
### Added
- Tailing log files
- Xconfmanager reload

## [0.2.1]
### Added
- Timeout configuration
### Changed
- Configuration moved to separate window.

## [0.1.3]
### Added
- Basic error handling.

## [0.1.2]
### Added
- Windchill basic control (stop/start/restart) with server add-on

## [0.1.1]
### Added
- Windchill status scanner
