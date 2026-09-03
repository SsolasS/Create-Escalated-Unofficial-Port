package rbasamoyai.escalated;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import rbasamoyai.escalated.config.EscalatedConfigs;
import rbasamoyai.escalated.index.EscalatedDataComponents;
import rbasamoyai.escalated.index.EscalatedTriggers;

public class CreateEscalatedFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CreateEscalated.REGISTRATE.register();
        CreateEscalated.init();

        ModGroup.register();

        // These are kept here for now. We will verify their 26.2
        // registration APIs during the first compilation pass.
        EscalatedDataComponents.register();
        EscalatedTriggers.register();

        ServerTickEvents.END_SERVER_TICK.register(CreateEscalated::onServerTick);

        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                CreateEscalated.onServerStopping());

        EscalatedConfigs.register();
    }
}
