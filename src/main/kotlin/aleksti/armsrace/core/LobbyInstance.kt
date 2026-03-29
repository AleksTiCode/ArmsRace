package aleksti.armsrace.core

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class LobbyInstance(val id: Int, val template: LobbyTemplate) {
    val players = mutableListOf<ServerPlayer>()
    var state = GameState.WAITING

    fun start(): String {
        if (state == GameState.WAITING && players.size >= 1) {
            return try {
                state = GameState.PLAYING

                for ((index, player) in players.withIndex()) {
                    val spawn = template.spawns.getOrNull(index) ?: return "Недостаточно точек спавна"
                    player.teleportTo(spawn.x, spawn.y, spawn.z)
                    player.inventory.clearContent()
                    player.inventory.setItem(0, ItemStack(Items.WOODEN_SWORD))
                    player.inventory.selected = 0
                }

                "Успешная телепортация"
            } catch (e: Exception) {
                "Ошибка $e"
            }
        }

        return "Игроков мало или игра уже идет"
    }
}
