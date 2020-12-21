# Changelog

## [1.0.2]
### Added
- Persistable custom tabs [loosing custom opened logs after IDE restart #51](https://github.com/d-wojciechowski/plm-companion/issues/51)

## [1.0.1]
### Fixed
- Compatibility with Intellij 2020.3

## [1.0.0]
### Added
- New startup activity which displays plugin info page with links and support section.
- Plugin UI is now backed by i18n resources. If you want to translate plugin UI to your language, contact me!
- Describe property dialog, which stores all described properties, as shortcuts.
### Changed
- Plugin Icon to a new, more suitable one.
- Updated description to better explain a plugin.
- Presentation of executed commands/properties in bottom panel (see commands execution).
### Fixed
- Rendering of form based UI. 

## [0.9.1]
### Added
- Compatibility with 2020.2

## [0.9.0]
### Added
- Wrapping enable/disable option on command pane
- Auto-scroll option for log panes and command pane
- In command list under RMB added "Rerun" option.
- Validation in RemoteFilePicker for situations where expected is fileOnly.
- Double click selection in RemoteFilePicker.
### Changed
- Introduced better [changelog format](https://github.com/JetBrains/gradle-changelog-plugin)
- By default, website scanning is turned on (was turned off).
- Order of icons next to panes ( according to user feedback )
- Navigation bar actions, Load from file action and Buttons in right pane may now automatically open command panel (may be switched off in settings)
- Xconfmanager may now be executed regardless of windchill status.
- Bottom pane buttons unification.
### Fixed
- [Issue](https://github.com/d-wojciechowski/plm-companion/issues/27) with encoding when, addon is on Windows system, and Intellij is running on MacOS

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
