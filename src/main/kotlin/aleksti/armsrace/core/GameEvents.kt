package aleksti.armsrace.core

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent

object GameEvents {

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        val entity = event.entity
        if (entity !is Zombie) return
        val source = event.source.entity
        if (source !is ServerPlayer) return
        val level = LobbyManager.playerLevels[source.uuid] ?: return
        val lobby = LobbyManager.findLobbyByPlayer(source) ?: return
        if (lobby.state != GameState.PLAYING) return
        val newLevel = level + 1
        LobbyManager.playerLevels[source.uuid] = newLevel
        val item = LobbyManager.weapons.getOrNull(newLevel)
        if (item == null) {
            for (player in lobby.players) {
                player.sendSystemMessage(Component.literal("Победил ${source.gameProfile.name}"))
            }
            LobbyManager.deleteLobby(lobby.id)
        } else {
            val stack = ItemStack(item)
            source.inventory.setItem(0, stack)
            source.inventory.selected = 0
        }
    }
}