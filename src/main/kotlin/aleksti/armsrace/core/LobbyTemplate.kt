package aleksti.armsrace.core
import kotlinx.serialization.Serializable

enum class GameState {
    WAITING,
    PLAYING,
    LOBBY
}

@Serializable
data class SpawnPoint(
    val x: Double,
    val y: Double,
    val z: Double,
//    val world: String
)

@Serializable
data class LobbyTemplate(
    val template_id: String,
    val spawns: List<SpawnPoint>,
    val instantRespawn: Boolean = true,
//    val requiredKillsToWin: Int = 3,
    val allowBlockBreaking: Boolean = false,
    val weapons: List<String>,
    val type: String = "Team",
    val minPlayers: Int = 1,
    val allPlayersNeed: Boolean = false,
    val waitingWarmUp: Boolean = true,
)
