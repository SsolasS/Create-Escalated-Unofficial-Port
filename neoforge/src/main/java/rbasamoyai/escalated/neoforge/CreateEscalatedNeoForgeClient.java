package rbasamoyai.escalated.neoforge;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.EscalatedClientCommon;
import rbasamoyai.escalated.index.EscalatedBlockPartials;

@Mod(value = CreateEscalated.MOD_ID, dist = Dist.CLIENT)
public class CreateEscalatedNeoForgeClient {
    public CreateEscalatedNeoForgeClient(IEventBus modBus) {
        IEventBus forgeBus = NeoForge.EVENT_BUS;

        EscalatedBlockPartials.init();

        modBus.addListener(CreateEscalatedNeoForgeClient::onClientSetup);

        forgeBus.addListener(CreateEscalatedNeoForgeClient::onClientGameTick);
    }

    public static void onClientSetup(FMLClientSetupEvent evt) {
        evt.enqueueWork(() -> {
            EscalatedClientCommon.onClientSetup();
        });
    }

    public static void onClientGameTick(ClientTickEvent.Post evt) {
        EscalatedClientCommon.onClientGameTick(Minecraft.getInstance());
    }

}
