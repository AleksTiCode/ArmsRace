package aleksti.armsrace.core

import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.monster.Zombie
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.item.ItemTossEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent

object GameEvents {

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        val entity = event.entity ?: return
        val source = event.source.entity ?: return // as? ServerPlayer ?: return
        if (source is ServerPlayer) {
            val lobby = LobbyManager.findLobbyByPlayer(source) ?: return
            val level = LobbyManager.playerLevels[source.uuid] ?: return
            val spawn = lobby.template.spawns.random()
//            val level2 = LobbyManager.playerLevels[entity.uuid] ?: return
            if (lobby.state == GameState.LOBBY) return
            val newLevel = level + 1
            LobbyManager.playerLevels[source.uuid] = newLevel
            val item = lobby.template.weapons.getOrNull(newLevel)
            if (item == null) {
                for (player in lobby.players) {
                    player.sendSystemMessage(Component.literal("Победил ${source.gameProfile.name}"))
                }
                LobbyManager.deleteLobby(lobby.template.id)
            } else {
                val stack = ItemStack(item)
                source.inventory.setItem(0, stack)
                source.inventory.selected = 0
            }
        }
        if (entity is ServerPlayer && source is Zombie) {
            val lobby = LobbyManager.findLobbyByPlayer(entity) ?: return
            if (lobby.state != GameState.PLAYING) return
            if (lobby.template.instantRespawn == false) return
            val spawn = lobby.template.spawns.random()
            event.isCanceled = true
            entity.health = 20f
            entity.teleportTo(spawn.x, spawn.y, spawn.z)
        }

    }

    @SubscribeEvent
    fun onEntityDeathAndRespawnFalse(event: PlayerEvent.PlayerRespawnEvent) {
        val entity = event.entity as? ServerPlayer ?: return
        val lobby = LobbyManager.findLobbyByPlayer(entity) ?: return
        if (lobby.state == GameState.LOBBY) return
        val spawn = lobby.template.spawns.random()
        entity.teleportTo(spawn.x, spawn.y, spawn.z)
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity as? ServerPlayer ?: return
        if (player.level().isClientSide) {
            val food = player.foodData
            food.foodLevel = 20
            food.setSaturation(5.0f)
        }
    }

    @SubscribeEvent
    fun onEntityDrop(event: ItemTossEvent) {
        val entity = event.entity as? ServerPlayer ?: return
        val lobby = LobbyManager.findLobbyByPlayer(entity) ?: return
        if (lobby.state != GameState.PLAYING) return
        event.isCanceled = true
    }

    @SubscribeEvent
    fun onEntityDeathDrop(event: LivingDropsEvent) {
        val entity = event.entity as? ServerPlayer ?: return
        val lobby = LobbyManager.findLobbyByPlayer(entity) ?: return
        if (lobby.state != GameState.PLAYING) return
        event.isCanceled = true
    }
}