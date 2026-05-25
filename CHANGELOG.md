# Changelog for GottschCore 1.20.1

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.8.0] - 2026-05-25

### Added
- New `command` package with shared formatting tools for admin and debug commands.
  - `CommandResponseFormatter` — builds coloured status messages (success, failure, warning, info) in shorthand or full form. Works with either plain `Component` objects or translation keys. Includes `formatConfirmPrompt()` for two-step destructive command flows.
  - `ReportBuilder` — fluent builder for structured reports (e.g. `inspect` commands). Auto-aligns label column, supports sections, blank lines, and notes.
  - `FormatterConstants` — shared box-drawing characters, semantic icons, style helpers, clickable/suggestable component builders, and `buildConfirmButton()` for confirmation prompts.
- `DimensionCoords` — immutable coordinate class that extends `Coords` with a `ResourceKey<Level>` dimension field. Supports `save()`/`load()` NBT serialization (adds a `"dimension"` string tag alongside x/y/z). Defaults to the overworld when loading old data without a dimension tag. Factory methods: `DimensionCoords.of(dimension, x, y, z)`, `of(dimension, BlockPos)`, `of(dimension, ICoords)`.

## [2.6.0] - 2026-03-26

### Changed
- Update BST - dimensional aware.
- ProximitySpawnerBlockEntity.
- Box added new methods.
- SpawnUtil.

### Added
- MobCount, MobSetData, MobSetDataHandler, MobSetDataRegistry, WeightedMob
- StructureMobSetBlock, ProximityMobSetSpawnerBlockEntity, StructureMobSetBlockEntity
- MobSetConfiguration

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
