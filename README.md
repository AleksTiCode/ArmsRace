# 🔫 Arms Race (Mini-Game Mod)
**⚠️ EARLY ALPHA / EXPERIMENTAL:** *This mod is currently in early development. Core mechanics work, but full compatibility with other mods is still being tested. Bug reports and feedback are highly appreciated!*

Welcome to **Arms Race** — a fully customizable, server-side friendly mini-game mod for NeoForge!

Bring the classic "Gun Game" experience to your Minecraft server. Players battle each other to upgrade their weapons. The first player to get a kill with the final weapon on the list wins the match!

This mod is an independent core. It relies on vanilla mechanics, meaning it can technically issue ANY item as a weapon (Vanilla swords, bows, etc.).

### ✨ Current Features
*   **Customizable Arenas:** Create multiple arenas via a simple JSON config.
*   **Team Support:** Set up Free-For-All or Team Deathmatch modes.
*   **Custom Weapon & Armor Ladders:** Define your own progression list using item IDs. You can configure automatic armor equipping and extra items (like ammo or shields) for each level!
*   **Dynamic UI:** Built-in clean Scoreboard to track kills, lobby status, and warmup timers.
*   **Safe Environment:** Configurable block-breaking protection and spawn point management.

### 🚀 Roadmap / Planned Features
Since the mod is in its early stages, here is what is planned for the future updates:
*   **🔫 Weapon Mods Compatibility:** Thorough testing and guaranteed support for heavy gun mods like **Timeless and Classics Zero (TaC:Z)** and **Vic's Point Blank**.
*   **🌐 Cross-Loader Support:** Porting the mod to **Fabric** (potentially using Architectury API) and updating to other Minecraft versions.
*   **🔌 Plugin Compatibility:** Ensuring seamless work with popular server plugins (e.g., LuckPerms, economy, and anti-cheats).
*   **🏆 End-Game Polish:** Fancy victory screens, post-match statistics, and rewards.

### 📜 Commands (Requires OP / Permission Level 2 for setup)
*   `/armsrace create <template_id>` - Creates a lobby based on the config template.
*   `/armsrace join` - Joins an available lobby.
*   `/armsrace leave` - Leaves the current lobby.
*   `/armsrace start` - Force starts the game (bypasses the warmup).
*   `/armsrace setteam <player> <team_id>` - Moves a player to a specific team.
*   `/armsrace reload` - Reloads the JSON config without restarting the server!

### 🛠️ Configuration Guide
When you run the mod for the first time, it will generate an advanced default configuration file located at `config/armsrace_arenas.json`.

The config allows deep customization of weapons, team spawns, armor progression, and even specific inventory slots!

**Example Configuration:**
```json[
    {
        "templateId": "vanilla",
        "lobbyCoord": { "x": 137.0, "y": -54.0, "z": 0.0 },
        "maxPlayers": 10,
        "warmupTime": 60,
        "teams":[
            {
                "teamId": "1",
                "colorCode": "§b",
                "spawns":[ { "x": 143.0, "y": -57.0, "z": 28.0 } ]
            },
            {
                "teamId": "2",
                "colorCode": "§a",
                "spawns":[ { "x": 80.0, "y": -60.0, "z": 8.0 } ]
            }
        ],
        "weapons":[
            {
                "item": "minecraft:wooden_sword"
            },
            {
                "item": "minecraft:iron_sword",
                "additionalItems":[
                    { "item": "minecraft:grass_block", "count": 3, "slot": 2 }
                ]
            },
            {
                "item": "minecraft:diamond_sword"
            }
        ],
        "armor":[
            { "helmet": "minecraft:iron_helmet", "level": 0 },
            { "chestplate": "minecraft:iron_chestplate", "level": 1 }
        ],
        "additionalItems":[
            { "item": "minecraft:cobblestone", "count": 54, "slot": 7 }
        ]
    }
]
```

#### Advanced Parameters Explained:

`lobbyCoord` - The waiting area where players spawn before the match starts or after they leave.

`weapons` - The progression ladder. You can now define additionalItems for specific weapon levels (e.g., giving 30 rounds of ammo only when a player reaches the AK-47 level!).

`armor` - Equip players with specific armor pieces at specific levels.

`additionalItems` (Global) - Items given to all players at the start of the match and on every respawn (like building blocks or food). Define the count and the exact inventory slot.