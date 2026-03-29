package aleksti.armsrace.core

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items
import java.util.UUID

object LobbyManager {
    val activeLobbies = mutableMapOf<Int, LobbyInstance>()
    val playerLevels = mutableMapOf<UUID, Int>()
    var id = 0

    val weapons = listOf(
        Items.WOODEN_SWORD,
        Items.STONE_SWORD,
        Items.IRON_SWORD,
        Items.DIAMOND_SWORD
    )

    fun createLobby(): String {
        id += 1
        val template = LobbyTemplate(
            listOf(
                SpawnPoint(50.0, 60.0, 80.0),
//                    SpawnPoint(70.0, 80.0, 80.0)
            )
        )
        activeLobbies[id] = LobbyInstance(id, template)
        return "Успешно создано лобби $id"
    }

    fun findLobbyByPlayer(player: ServerPlayer): LobbyInstance? {
        for (instance in activeLobbies.values) {
            if (instance.players.contains(player)) {
                return instance
            }
        }
        return null
    }

    fun deleteLobby(lobbyID: Int) {
        val lobby = activeLobbies[lobbyID] ?: return
        for (player in lobby.players)  {
            player.teleportTo(0.0, 0.0, 0.0)
            player.inventory.clearContent()
            playerLevels.remove(player.uuid)
        }
        activeLobbies.remove(lobbyID)
    }

    fun addPlayer(player: ServerPlayer, id: Int? = null): String {
        if (findLobbyByPlayer(player) != null) {
            return "Вы уже в лобби"
        }
        if (id == null) {
            for (instance in activeLobbies.values) {
                if (instance.state == GameState.WAITING && instance.players.size < instance.template.spawns.size) {
                    instance.players.add(player)
                    playerLevels[player.uuid] = 0
                    return "Вы успешно присоединились к лобби"
                }
            }
            return "Нет доступного лобби"
        } else {
            val lobby = activeLobbies[id] ?: return "Лобби не найдено"
            lobby.players.add(player)
            playerLevels[player.uuid] = 0
            return "Вы успешно присоединились к лобби $id"
        }
    }

    fun removePlayer(player: ServerPlayer): String {
        val lobby = findLobbyByPlayer(player) ?: return "Вы не в лобби"
        player.teleportTo(0.0, 0.0, 0.0)
        player.inventory.clearContent()
        lobby.players.remove(player)
        playerLevels.remove(player.uuid)
        return "Вы вышли из игры"
    }

    fun startCommandByPlayer(player: ServerPlayer) : String {
        val lobby = findLobbyByPlayer(player) ?: return "Вас нет в лобби"
        return lobby.start()
    }

    fun startCommand(lobbyID: Int): String {
        val lobby = activeLobbies[lobbyID] ?: return "Лобби не найдено"
        return lobby.start()
    }
}
