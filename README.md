## 🚨 Mod in alpha version. If you notice any errors, open issues on GitHub 🚨

# 🔫 Arms Race (Mini-Game Mod)
Welcome to **Arms Race** — a fully customizable, server-side friendly mini-game mod for NeoForge!

Bring the classic "Gun Game" experience to your Minecraft server. Players battle each other to upgrade their weapons. The first player to get a kill with the final weapon on the list wins the match!

This mod is an independent core. It doesn't add new weapons by itself, which makes it **100% compatible** with your favorite gun mods like **Timeless and Classics Zero (TaC:Z)**, **Vic's Point Blank**, or even standard Vanilla Minecraft swords and bows!

### ✨ Features
*   **Fully Customizable Arenas:** Create multiple arenas via a simple JSON config.
*   **Team Support:** Set up Free-For-All or Team Deathmatch modes.
*   **Custom Weapon Ladders:** Define your own weapon progression list using item IDs (e.g., `minecraft:iron_sword` or `tacz:ak47`).
*   **Dynamic UI:** Built-in clean Scoreboard to track kills, lobby status, and warmup timers.
*   **Safe Environment:** Configurable block-breaking protection and spawn point management.

### 📜 Commands (Requires OP / Permission Level 2 for setup)
*   `/armsrace create <template_id>` - Creates a lobby based on the config template.
*   `/armsrace join` - Joins an available lobby.
*   `/armsrace leave` - Leaves the current lobby.
*   `/armsrace start` - Force starts the game (bypasses the warmup).
*   `/armsrace setteam <player> <team_id>` - Moves a player to a specific team.
*   `/armsrace reload` - Reloads the JSON config without restarting the server!

### 🛠️ Configuration Guide
When you run the mod for the first time, it will generate a default configuration file located at `config/armsrace_arenas.json`.

You can add as many arena templates as you want! Here is a breakdown of the structure:

```json[
  {
    "template_id": "dust2_arena",
    "displayName": "Dust 2 - Deathmatch",
    "teams":[
      {
        "teamId": "red",
        "colorCode": "§c",
        "spawns":[
          { "x": 10.5, "y": 65.0, "z": 20.5 },
          { "x": 12.0, "y": 65.0, "z": 22.0 }
        ]
      },
      {
        "teamId": "blue",
        "colorCode": "§9",
        "spawns":[
          { "x": -10.5, "y": 65.0, "z": -20.5 },
          { "x": -12.0, "y": 65.0, "z": -22.0 }
        ]
      }
    ],
    "weapons":[
      "minecraft:wooden_sword",
      "minecraft:iron_sword",
      "minecraft:diamond_sword",
      "minecraft:golden_sword"
    ],
    "minPlayers": 2,
    "maxPlayers": 10,
    "warmupTimeSeconds": 15,
    "allowBlockBreaking": false
  }
]
```

#### Parameters Explained:
*  `template_id` - The system name used in the /armsrace create command.
*  `displayName` - The title displayed on the players' Scoreboard.
*  `colorCode` - Minecraft formatting code (e.g., §c for Red) to color player names on the scoreboard.
*  `weapons` - The progression list. Players upgrade to the next item upon getting a kill. The last item is the winning weapon!
*  `allowBlockBreaking` - If false, protects your arena from griefing during the match.