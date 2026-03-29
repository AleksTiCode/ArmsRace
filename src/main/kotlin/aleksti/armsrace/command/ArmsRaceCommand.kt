package aleksti.armsrace.command

import aleksti.armsrace.core.LobbyManager
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.RangeArgument
import net.minecraft.network.chat.Component

class ArmsRaceCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("armsrace")
                .then(Commands.literal("create")
                    .requires { sourceStack -> sourceStack.hasPermission(2) }
                    .executes { ctx ->
                        ctx.source.sendSuccess({ Component.literal(LobbyManager.createLobby()) }, false)
                        1
                    }
                )
                .then(Commands.literal("join")
                    .executes { ctx ->
                        ctx.source.sendSuccess({ Component.literal(LobbyManager.addPlayer(ctx.source.playerOrException)) }, false)
                        1
                    }
                    .then(Commands.argument("lobby_id", IntegerArgumentType.integer(1))
                        .executes {ctx ->
                            ctx.source.sendSuccess({ Component.literal(LobbyManager.addPlayer(ctx.source.playerOrException,
                                IntegerArgumentType.getInteger(ctx, "lobby_id"))) }, false)
                            1
                        })
                )
                .then(Commands.literal("start")
                    .requires { sourceStack -> sourceStack.hasPermission(2) }
                    .executes { ctx ->
                        ctx.source.sendSuccess({ Component.literal(LobbyManager.startCommandByPlayer(ctx.source.playerOrException)) }, false)
                        1
                    }
                    .then(Commands.argument("start_id", IntegerArgumentType.integer(1))
                        .executes {ctx ->
                            ctx.source.sendSuccess({ Component.literal(LobbyManager.startCommand(IntegerArgumentType.getInteger(ctx, "start_id")))}, false)
                            1
                        })
                )
                .then(Commands.literal("leave")
                    .executes { ctx ->
                        ctx.source.sendSuccess({ Component.literal(LobbyManager.removePlayer(ctx.source.playerOrException)) }, false)
                        1
                    }
                )
        )
    }
}
