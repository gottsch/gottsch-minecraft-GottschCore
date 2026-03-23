**# Changelog for GottschCore 1.20.1

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.6.1] - TBD

- Update Box...

## [2.5.1] - 2025-09-23

### Changed
- Fixed AbstractProximinityBlockEntity load() method to properly check for "proximity" value.
  - this had prevented proximity from loading properly and always defaulting to the 5D value.

## [2.5.0] - 2025-06-15

### Added

- Interval implementation for bst package.

## [2.4.0] - 2025-05-09

### Added

- IHalfBlock block interface
- HalfBlock block
- FacingHalfBlock
- WaterloggedFacingHalfBlock block
- ModUtil class - contains asLocation(), mcLocation() methods, spawnMob(), spawnAndAddMob() methods.

## [2.3.0] - 2024-10-27

### Changed
- deprecated usage of Coords constructors in favor of static of() methods.

## [2.2.0] - 2024-08-20

### Changed
- changed WeightedCollection.add() to synchronized. 

### Added
- WeightedCollection.remove(T key) method.
