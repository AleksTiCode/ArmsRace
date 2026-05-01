package aleksti.armsrace.core

import aleksti.armsrace.core.LobbyManager.getItemFromString
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.item.ItemTossEvent
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.level.BlockEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent

object GameEvents {

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        val entity = event.entity ?: return
        val source = event.source.entity ?: return // as? ServerPlayer ?: return
        if (source is ServerPlayer) {
            val lobby = LobbyManager.findLobbyByPlayer(source) ?: return
            val level = LobbyManager.playerLevels[source.uuid] ?: return
//            val spawn = lobby.template.spawns.random()
//            val level2 = LobbyManager.playerLevels[entity.uuid] ?: return
            if (lobby.state == GameState.LOBBY) return
            val newLevel = level + 1
            LobbyManager.playerLevels[source.uuid] = newLevel
            val index = lobby.template.weapons.getOrNull(newLevel)
            if (index == null) {
                for (player in lobby.players.keys) {
                    ScoreboardManager.removeScoreboard(player)
                    player.connection.send(ClientboundSetTitlesAnimationPacket(10, 60, 20))
                    player.connection.send(ClientboundSetTitleTextPacket(Component.literal("§6§lИГРА ОКОНЧЕНА")))
                    player.connection.send(ClientboundSetSubtitleTextPacket(Component.literal("§fПобедил: §a${source.displayName?.string ?: source.name.string}")))
                }
                LobbyManager.deleteLobby(lobby.id)
            } else {
                source.inventory.setItem(0, ItemStack(getItemFromString(index)))
                source.inventory.selected = 0
                source.displayClientMessage(Component.literal("§eОружие: ${newLevel}/${lobby.template.weapons.size}"), true)
                source.playNotifySound(SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f)
                for (player in lobby.players.keys) ScoreboardManager.updateScoreboard(player, lobby)
            }
        }
        if (entity is ServerPlayer) {
            val lobby = LobbyManager.findLobbyByPlayer(entity) ?: return
            if (lobby.state == GameState.LOBBY) return
            if (lobby.template.instantRespawn == false) return
//            val spawn = lobby.template.spawns.random()
            event.isCanceled = true
            lobby.teleportPlayerToSpawn(entity)
//            entity.health = 20f
//            entity.teleportTo(spawn.x, spawn.y, spawn.z)
        }

    }

    @SubscribeEvent
    fun onEntityDeathAndRespawnFalse(event: PlayerEvent.PlayerRespawnEvent) = runIfInGame(event.entity) {player, lobby ->
        lobby.teleportPlayerToSpawn(player)
    }

    @SubscribeEvent
    fun onPlayerTick(event: ServerTickEvent.Post) {
        for (lobby in LobbyManager.activeLobbies.values) {
            if (lobby.state != GameState.LOBBY) {
                for (player in lobby.players.keys) {
                    val food = player.foodData
                    food.foodLevel = 20
                    food.setSaturation(5.0f)
                }
            }
        }
    }

    @SubscribeEvent
    fun onEntityDrop(event: ItemTossEvent) = runIfInGame(event.entity) { player, lobby ->
        event.isCanceled = true // Запрещаем выбрасывать на Q
    }

    @SubscribeEvent
    fun onEntityDeathDrop(event: LivingDropsEvent) = runIfInGame(event.entity) { player, lobby ->
        event.isCanceled = true // Запрещаем выпадение лута при смерти
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Post) {
        for (lobby in LobbyManager.activeLobbies.values) lobby.tick()
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) = runIfInGame(event.player) { player, lobby ->
        if (!lobby.template.allowBlockBreaking == false) event.isCanceled = true
    }

    @SubscribeEvent
    fun onBlockPlace(event: BlockEvent.EntityPlaceEvent) = runIfInGame(event.entity) { player, lobby ->
        if (!lobby.template.allowBlockBreaking == false) event.isCanceled = true
    }

    @SubscribeEvent
    fun onPlayerDamage(event: LivingIncomingDamageEvent) = runIfInGame(event.entity) { player, lobby ->
        val source = event.source as? ServerPlayer ?: return
        if (lobby.players[player] == lobby.players[source]) event.isCanceled = true
    }

    private inline fun runIfInGame(entity: Entity?, action: (ServerPlayer, LobbyInstance) -> Unit) {
        val player = entity as? ServerPlayer ?: return
        val lobby = LobbyManager.findLobbyByPlayer(player) ?: return
        if (lobby.state != GameState.LOBBY) {
            action(player, lobby) // Вызываем переданную логику
        }
    }
}