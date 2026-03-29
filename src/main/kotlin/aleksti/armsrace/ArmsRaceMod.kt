package aleksti.armsrace

import aleksti.armsrace.command.ArmsRaceCommand
import aleksti.armsrace.core.GameEvents
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent

const val ID = "armsrace"
@Mod(ID)
class ArmsRaceMod(modEventBus: IEventBus) {

    init {
        modEventBus.addListener(this::onCommonSetup)
        NeoForge.EVENT_BUS.register(this)
        NeoForge.EVENT_BUS.register(GameEvents)
    }

    fun onCommonSetup(event: FMLCommonSetupEvent) {
        println("Common setup")
    }

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        ArmsRaceCommand().register(event.dispatcher)
    }
}