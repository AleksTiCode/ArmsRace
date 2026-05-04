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
data class TeamTemplate(
    val teamId: String, // Например: "red", "blue" или "terrorists"
    val colorCode: String = "§f",
    val spawns: List<SpawnPoint>
)


@Serializable
data class LobbyTemplate(
    val templateId: String,
    val displayName: String = "§6§lГОНКА ВООРУЖЕНИЙ",
    val teams: List<TeamTemplate>,
    val instantRespawn: Boolean = true,
    val allowBlockBreaking: Boolean = false,
    val weapons: List<String>,
    val minPlayers: Int = 2,
    val maxPlayers: Int,
    val warmupTime: Int = 60,
    val warmup: Boolean = true,
    val lobbyCoord: SpawnPoint,
)
