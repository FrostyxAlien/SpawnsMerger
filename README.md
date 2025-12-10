# SpawnsMerger

SpawnsMerger is a Paper plugin that introduces limited-use mob spawners and a simple merging system. Players can generate spawners with a fixed number of remaining spawns, place them, and combine compatible spawners through a GUI to extend their lifespan.

## Features
- **Limited spawner items:** `/givespawner` creates a spawner item tagged with a mob type and a remaining spawn count using the NBT-API library.
- **Placement support:** When a tagged spawner is placed, the block is configured to the stored mob type and the remaining count is saved in the spawner's persistent data container.
- **Spawn consumption:** Each spawn reduces the remaining counter; when it reaches zero the spawner block is removed.
- **Hologram feedback:** Right-clicking a spawner shows an armor-stand hologram with the mob type and remaining spawns, which is updated after each spawn and automatically cleared after a short delay.
- **Silk Touch drops:** Breaking a limited spawner with Silk Touch drops an item preserving the mob type and remaining spawns.
- **Merge GUI:** `/mergespawner` opens a 27-slot GUI with two input slots. Inserting two limited spawners of the same type combines their remaining counts into a single item.

## Requirements
- Paper (or compatible) server targeting Minecraft 1.21.x
- Java 21 runtime
- [NBT-API](https://www.spigotmc.org/resources/nbt-api.7939/) plugin present on the server (declared as a dependency in `plugin.yml`)

## Installation
1. Build the plugin: `./gradlew build`
2. Copy the generated JAR from `build/libs/` into your server's `plugins` folder.
3. Ensure NBT-API is installed, then start or reload the server.

## Commands
| Command | Description | Usage |
|---------|-------------|-------|
| `/givespawner <MobType> <Spawns>` | Gives the executing player a spawner item for the specified mob type with the given remaining spawn count. Only valid, spawnable mobs are accepted. | `/givespawner ZOMBIE 20` |
| `/mergespawner` | Opens the merge GUI. Place two compatible limited spawners into the left/right slots and click the anvil to combine them. Items are returned automatically if the GUI is closed. | `/mergespawner` |

## Gameplay Notes
- The hologram is anchored above the spawner block and fades automatically; breaking the spawner also clears the hologram.
- Spawners without the plugin's NBT tags behave like normal spawners and cannot be merged.
- Using Silk Touch is required to retrieve a placed limited spawner as an item; otherwise it breaks without drops.

## Development Overview
- **Entry point:** `SpawnsMerger` registers commands and listeners on enable/disable.
- **Command executors:** `SpawnerCommand` creates tagged spawner items; `SpawnerMergeCommand` opens the GUI for players.
- **Listeners:** `SpawnerListener` handles placement, interaction, spawning, and breaking logic, including hologram management. `SpawnerMergeGUI` manages the merge inventory interactions.

Contributions and bug reports are welcome! Feel free to open issues or pull requests for enhancements.
