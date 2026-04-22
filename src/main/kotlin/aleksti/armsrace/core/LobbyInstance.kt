package aleksti.armsrace.core

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class LobbyInstance( val template: LobbyTemplate) {
    val players = mutableListOf<ServerPlayer>()
    var state = GameState.LOBBY

    fun start(gameState: GameState): String {
        if (state != GameState.PLAYING && players.size <= template.spawns.size) {
            state = gameState
            for ((index, player) in players.withIndex()) {
                val spawn = template.spawns.getOrNull(index) ?: return "Недостаточно точек спавна"
                if (gameState == GameState.WAITING) LobbyManager.inventories[player.uuid] = player.inventory.items.map  {it.copy()}
                player.health = 20f
                player.teleportTo(spawn.x, spawn.y, spawn.z)
                player.inventory.clearContent()
                player.inventory.setItem(0, ItemStack(Items.WOODEN_SWORD))
                player.inventory.selected = 0
            }
        } else return "Игроков мало или игра уже идет"
        return "Игра началась"
    }
}
