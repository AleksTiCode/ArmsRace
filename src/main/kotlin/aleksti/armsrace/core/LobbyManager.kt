package aleksti.armsrace.core

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.UUID

object LobbyManager {
    val activeLobbies = mutableMapOf<Int, LobbyInstance>()
    val playerLevels = mutableMapOf<UUID, Int>()
    val inventories = mutableMapOf<UUID, List<ItemStack>>()

    fun createLobby(template_id: String): String {
        val id = activeLobbies.size + 1
        val template = ConfigManager.templates.find { it.templateId == template_id } ?: return "Арена не найдена!"
        activeLobbies[id] = LobbyInstance(id, template)
        return "Успешно создано лобби $template_id - $id"
    }

    fun findLobbyByPlayer(player: ServerPlayer): LobbyInstance? {
        for (instance in activeLobbies.values) {
            if (instance.players.contains(player)) {
                return instance
            }
        }
        return null
    }

    fun deleteLobby(lobbyID: Int?): String {
        val lobby = activeLobbies[lobbyID] ?: return "Такого лобби нет"
        lobby.state = GameState.LOBBY
        for (player in lobby.players.keys.toList()) removePlayer(player)
        activeLobbies.remove(lobbyID)
        return "Лобби удалено"
    }

    fun addPlayer(player: ServerPlayer, id: Int? = null): String {
        if (findLobbyByPlayer(player) != null) {
            return "Вы уже в лобби"
        }
        if (id == null) {
            for (lobby in activeLobbies.values) {
                val totalSpawns = lobby.template.teams.sumOf { it.spawns.size }
                if (lobby.state != GameState.PLAYING && lobby.players.size < totalSpawns) {
                    lobby.players[player] = ""
                    playerLevels[player.uuid] = 0
                    lobby.checkWarmup()
                    ScoreboardManager.updateScoreboard(player, lobby)
                    return "Вы успешно присоединились к лобби ${lobby.id}"
                }
            }
            return "Нет доступного лобби"
        } else {
            val lobby = activeLobbies[id] ?: return "Лобби не найдено"
            lobby.players[player] = ""
            playerLevels[player.uuid] = 0
            lobby.checkWarmup()
            ScoreboardManager.updateScoreboard(player, lobby)
            return "Вы успешно присоединились к лобби $id"
        }
    }

    fun removePlayer(player: ServerPlayer): String {
        val lobby = findLobbyByPlayer(player) ?: return "Вы не в лобби"
        val spawn = lobby.template.lobbyCoord
        player.inventory.clearContent()
        val savedItems = inventories.remove(player.uuid)
        savedItems?.forEachIndexed { index, itemStack ->
            player.inventory.setItem(index, itemStack)
        }
        lobby.players.remove(player)
        playerLevels.remove(player.uuid)
        ScoreboardManager.removeScoreboard(player)
        if (lobby.state != GameState.LOBBY) lobby.checkWarmup()
        player.teleportTo(spawn.x, spawn.y, spawn.z)
        player.health = 20f
        return "Вы вышли из игры"
    }

    fun startCommand(lobbyID: Int?): String {
        val lobby = activeLobbies[lobbyID] ?: return "Лобби не найдено"
        if (lobby.state == GameState.PLAYING) return "Игра уже идёт"
        if (lobby.state == GameState.WAITING) {
            for (player in lobby.players.keys) playerLevels[player.uuid] = 0
            return lobby.start(GameState.PLAYING)
        } else return lobby.start(GameState.WAITING)
    }

    fun getItemFromString(id: String): Item {
        val location = ResourceLocation.parse(id)
        // Если игра не найдет такой предмет, выдадим деревянный меч, чтобы игра не крашнулась
        return BuiltInRegistries.ITEM.getOptional(location).orElse(Items.AIR)
    }
}
