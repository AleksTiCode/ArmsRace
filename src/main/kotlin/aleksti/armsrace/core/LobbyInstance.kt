package aleksti.armsrace.core

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class LobbyInstance(val id: Int, val template: LobbyTemplate) {
    val players = mutableMapOf<ServerPlayer, String>()
    var state = GameState.LOBBY
    var warmupTicks = -1

    fun start(gameState: GameState): String {
        if (state != GameState.PLAYING && players.isNotEmpty()) {
            val availableTeams = template.teams.map { it.teamId }
            if (availableTeams.isEmpty()) return "Ошибка: в шаблоне нет команд!"
            state = gameState

            for ((index, player) in players.keys.toList().withIndex()) {
                val assignedTeamId = availableTeams[index % availableTeams.size]
                players[player] = assignedTeamId
                if (gameState == GameState.WAITING) LobbyManager.inventories[player.uuid] = player.inventory.items.map  {it.copy()}
                teleportPlayerToSpawn(player)
                player.inventory.clearContent()
                player.inventory.setItem(0, ItemStack(Items.WOODEN_SWORD))
            }
        } else return "Игроков мало или игра уже идет"
        return "Игра началась"
    }

    // Функция сама узнает команду игрока и телепортирует его куда надо
    fun teleportPlayerToSpawn(player: ServerPlayer) {
        // 1. Узнаем, за какую команду играет этот игрок (читаем из нашей Мапы)
        val teamId = players[player] ?: return

        // 2. Ищем настройки этой команды в шаблоне
        val teamData = template.teams.find { it.teamId == teamId } ?: return

        // 3. Берем случайный спавн и телепортируем
        if (teamData.spawns.isNotEmpty()) {
            val spawn = teamData.spawns.random()
            player.health = 20f
            player.inventory.selected = 0
            player.teleportTo(spawn.x, spawn.y, spawn.z)
            player.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 60, 255, false, false))
        }
    }

    fun checkWarmup() {
        if (template.warmup == true) {
            if (players.size == template.maxPlayers) start(GameState.PLAYING)
            else if (players.size >= template.minPlayers) {
                start(GameState.WAITING)
                warmupTicks = template.warmupTime * 20
            } else if (players.size < template.minPlayers) warmupTicks = -1
            else if (players.size == 0) LobbyManager.deleteLobby(id)
        } else {
            if (players.size == template.maxPlayers) start(GameState.PLAYING)
            else warmupTicks = -1
        }
    }

    fun tick() {
        if (state != GameState.WAITING || warmupTicks < 0) return

        warmupTicks-- // Отнимаем 1 тик

        if (warmupTicks % 20 == 0) {
            for (player in players.keys) {
                ScoreboardManager.updateScoreboard(player, this)
            }
        }

        // Если время вышло - стартуем!
        if (warmupTicks == 0) {
            start(GameState.PLAYING)
        }

        // (Для красоты) Если число делится на 20 без остатка (прошла ровно 1 секунда)
        // можно выводить сообщение в ActionBar или чат, если осталось 5, 4, 3...
    }
}
