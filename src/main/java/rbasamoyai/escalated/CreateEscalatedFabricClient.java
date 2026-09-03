package rbasamoyai.escalated;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import rbasamoyai.escalated.index.EscalatedBlockPartials;

public class CreateEscalatedFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EscalatedBlockPartials.init();

        EscalatedClientCommon.onClientSetup();

        ClientTickEvents.END_CLIENT_TICK.register(
                EscalatedClientCommon::onClientGameTick
        );
    }
}
