# Changelog for GottschCore 1.20.1

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.9.0] - 2026-07-05

### Fixed
- `SpawnUtil.spawnAndAddMob` now adds the created/positioned mob to the world. It previously added an un-positioned, passed-in instance and discarded the actual spawned mob, so mob-set proximity spawners spawned nothing.
- `ProximityMobSetSpawnerBlockEntity.execute` no longer creates and finalizes a mob twice; it delegates spawning to `SpawnUtil.spawnAndAddMob`.
- `MobSetDataHandler` now clears the registry at the start of each reload so removed/edited data packs no longer leave stale entries behind.
- `MobSetDataHandler` merge/replace now reads the `replace` flag from the incoming data instead of the existing entry.

### Changed
- `SpawnUtil` reworked so creation and world-insertion are separate steps: `spawnMob(...)` creates, positions and finalizes a mob but does not add it to the world (an interception point for external mods to modify the entity first); `spawnAndAddMob(...)` performs the add. Removed the ignored `Entity mob` parameters from both methods.
- `MobSetDataRegistry` backing map is now a `ConcurrentHashMap` (data pack reloads run off-thread); `get(null)` returns empty.
- `MobCount` constructor normalizes counts to be non-negative with `min <= max`.
- `WeightedMob` reduced to a plain record (removed redundant hand-written accessors and `equals`/`hashCode`/`toString`).

### Added
- **`StrictCodecs`** (`mod.gottsch.forge.gottschcore.json`) and its use throughout `world.gen.structure.templatesystem`: an optional field may now be **omitted**, but not **wrong**.
  - DFU's `optionalFieldOf(name, default)` cannot tell "absent" from "present and malformed" — `OptionalFieldCodec` maps any decode failure to an empty optional, so the default is substituted either way. Every field in the package was written that way, and the result was silent: a bad `probability` became `0.0` and the rule went inert; ONE bad entry in a `blocks` palette emptied the whole palette (a list codec fails as a unit) and the rule then reported itself inactive; a bad `floor_growth` object became `NONE`, losing the behaviour entirely. No error, no log line, pack loads, dungeon is merely plainer than authored.
  - All 19 optional fields across `AgingProcessor`, `AgingStage`, `BlockMatch`, `DecorationProcessor`, `DecorationRule` and `WallGrowthRule` now fail the file on a malformed value, with the field name prepended to the message. **Omitting a field still yields its default**, so packs that simply do not set a key are unaffected.
  - Deliberately narrower than a closed schema: an *undeclared* key is still ignored, since a shared library's consumers may legitimately carry spare fields. Closing the schema stays a decision for the consuming mod's own records.
- **Weighted decoration palettes** (`WeightedBlock`). `DecorationRule` and `WallGrowthRule` `blocks` lists now accept either a bare block id or `{"block": <id>, "weight": <int>}`, mixed freely in one list. A bare id means weight 1, so **every palette written before this keeps working unchanged and nothing needs migrating**; encoding round-trips weight 1 back to a bare id so re-serialising a pack does not rewrite it into the verbose form.
  - **An unweighted palette draws exactly as it did before.** The pick is one `nextInt(totalWeight)` walked over the cumulative weights; with all weights 1, `totalWeight == size` and the walk lands on the index the roll named — the same block, from the same single draw, as the previous `blocks.get(nextInt(size))`. That is deliberate and pinned by `WeightedBlockTest`: these palettes are drawn during worldgen from a position-seeded random, so a draw consuming a different amount of randomness would silently re-decorate every world that already exists.
  - `weight` is **required** in the object form, and 0 is legal (an entry parked without deleting it). A palette whose weights are all 0 reports its rule inactive rather than drawing nothing-shaped. Negative weights fail the decode. Required rather than defaulted because DFU's `optionalFieldOf(name, default)` cannot tell "absent" from "present and invalid" — with a default of 1, `"weight": -3` decoded silently as 1.
  - Rules built in code use `DecorationRule.of(probability, List<Block>)` / `WallGrowthRule.of(...)`, which wrap at weight 1.
- Multi-set support wired end-to-end: `StructureMobSetBlockEntity` now loads/saves its `mobSets` list, and `ProximityMobSetSpawnerBlockEntity` selects a set at trigger time from `mobSetNames` when no single `mobSetName` is set.
- Documentation on `MobSetDataHandler` describing the `mob_sets` data pack folder and the required `AddReloadListenerEvent` registration by consuming mods.
- New `world.gen.structure.templatesystem` package for shared processors built on **vanilla's** template system (`worldgen/processor_list` JSON), as opposed to the legacy `GottschTemplate` path in the parent package.
  - `AgingProcessor` — ages blocks along multi-stage decay chains and, unlike vanilla's `minecraft:rule`, **carries the source block's state properties onto the replacement**. A vanilla `ProcessorRule` emits a fixed `output_state` and drops properties, so ageing a stair/slab/wall with it silently resets facing/half/shape; this copies every property the source and replacement share, letting one rule age a whole family of shaped blocks. Chains are graduated: a stage is only reachable if the stage before it was rolled, capped by `agings`.
  - `AgingRule` / `AgingStage` — the datapack model (`block` / `output_blocks` / `probability`). Block ids are resolved by the codec, so a typo fails the file at load instead of producing a rule that never fires.
  - Randomness is derived from the block's absolute world position (`Mth.getSeed(pos)`, as vanilla's `RuleProcessor` does) rather than from `StructurePlaceSettings`, so results are identical regardless of caller — required by callers that run a processor list over procedurally-built blocks, where a piece is processed once per chunk it overlaps and a block on a chunk seam must resolve the same way in both passes.
  - **Registration is the consuming mod's job.** GottschCore registers no `StructureProcessorType` (most dependants never touch structure processors), so the class takes a `Supplier<StructureProcessorType<?>>` and exposes `AgingProcessor.codec(supplier)` instead of a static `CODEC`. The registration idiom is documented on the class.
  - Note: alternative chains for the same source block are tried in order and the first that decays wins, so a later chain's authored probability is **conditional** on the earlier ones missing — two alternatives that should each fire 30% of the time are authored `0.3` and `0.43`.
  - `DecorationProcessor` — decoration that depends on a block's **neighbours** rather than on the block alone, which neither `minecraft:rule` nor `AgingProcessor` can express since both decide one block at a time. Behaviours (all off until a datapack gives them a probability and a palette): `cobwebs`, `wall_growth` (clustering multiface growth on full-cube faces), `floor_growth` / `hanging_growth` (above and below a `dirt` match), `underwater_growth` / `floating_growth`, and `unsupported` (deletes blocks left hanging once nothing holds them up). Checks are independent, not a cascade, and only cells the piece itself places as air are ever overwritten. Same `codec(supplier)` registration idiom as `AgingProcessor`.
  - `BlockMatch` / `DecorationRule` / `WallGrowthRule` — the datapack model for it.
  - `LevelIndependentProcessor` — marker for a processor that never touches the level it is handed, deciding purely from the block list, the states in it, and their positions. That property is what lets a caller run a processor over a procedurally-built piece's **whole** block list instead of only the slice inside the chunk being generated: reading outside the active `WorldGenRegion` is illegal, so a processor that reads must be clipped and one that doesn't, needn't be. Neighbour-aware processors *must* be unclipped (a neighbour map built from one chunk's slice is missing everything across the seam); per-block ones benefit, because being unclipped keeps them in the same pass and therefore in authored order. `AgingProcessor` and `DecorationProcessor` both implement it. Callers that build blocks procedurally are expected to split their processor list into two passes on this marker; vanilla's own placement path never needs it.
- Test source set (JUnit 5, `useJUnitPlatform()`), GottschCore's first, with `AgingProcessorTest` covering property carry-over for stairs/walls/slabs, waterlogging survival, chain-stops-on-miss, the `agings` cap, alternative-chain precedence, and positional determinism, and `DecorationProcessorTest` covering each decoration behaviour, their independence, seam determinism, and the facing/rotation round trip.

### Deprecated
- `world.gen.structure.StructureProcessor` (the legacy `GottschTemplate` placement path). New processors should extend vanilla's `net.minecraft...templatesystem.StructureProcessor` and register a `StructureProcessorType`, so they are datapack-authored via `worldgen/processor_list` and apply to jigsaw/template placement for free.
  - **Not scheduled for removal** — `GottschTemplate` is still in use and this is a direction marker for new code, not a removal notice. Consuming mods that extend it will now see deprecation warnings.
  - The legacy type and vanilla's share a simple name but are unrelated and not interchangeable; that is why the new processors live in a `templatesystem` sub-package, where they cannot shadow vanilla's class on import.

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
