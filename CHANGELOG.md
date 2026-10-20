# Changelog for GottschCore (Forge 1.12.2)

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.15.1] - 2026-10-19

### 🛠️ Fixed
- Decaying a structure next to the bottom of the world no longer hangs the server. Backfilling under a decayed block now stops at the world's minimum height instead of looping forever.
- The log is no longer filled with "is file system loot table current" ERROR lines every time a world loads. It is now a debug message.

### ⚙️ Changed
- The minimum Forge version is now 14.23.5.2847 (was 2854), so GottschCore and the mods that use it run in RetroFuturaGradle's development environment.
- Built with RetroFuturaGradle and Gradle 8.10.

---

Changes for 1.15.0 and earlier are in the [GottschCore changelog gist](https://gist.github.com/gottsch/75a22a51a52d0fe8bf5f6711d4a65700).
