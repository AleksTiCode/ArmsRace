package aleksti.armsrace.core

import net.minecraft.world.item.Item

data class LobbyTemplate(
    val id: Int = 1,
    val spawns: List<SpawnPoint>,
    val instantRespawn: Boolean = true,
//    val requiredKillsToWin: Int = 3,
    val allowBlockBreaking: Boolean = false,
    val weapons: List<Item>,
    val type: String = "Team",
    val minPlayers: Int = 1,
    val allPlayersNeed: Boolean = false,
    val waitingWarmUp: Boolean = true,
)
