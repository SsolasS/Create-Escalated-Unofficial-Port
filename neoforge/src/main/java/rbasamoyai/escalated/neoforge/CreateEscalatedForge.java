package rbasamoyai.escalated.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.config.EscalatedConfigs;

@Mod(CreateEscalated.MOD_ID)
public class CreateEscalatedForge {

    public CreateEscalatedForge(IEventBus modBus) {
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        ModLoadingContext mlContext = ModLoadingContext.get();

        CreateEscalated.REGISTRATE.registerEventListeners(modBus);

        CreateEscalated.init();
        ModGroupImpl.registerForge(modBus);

        EscalatedConfigs.registerConfigs(mlContext::registerConfig);

        modBus.addListener(this::onLoadConfig);
        modBus.addListener(this::onReloadConfig);

        forgeBus.addListener(this::onServerTick);
        forgeBus.addListener(this::onServerStopping);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> EscalatedClientForge.prepareClient(modBus, forgeBus));
    }

    private void onLoadConfig(ModConfigEvent.Loading evt) { EscalatedConfigs.onLoad(evt.getConfig()); }

    private void onReloadConfig(ModConfigEvent.Reloading evt) { EscalatedConfigs.onReload(evt.getConfig()); }

    private void onServerTick(final TickEvent.ServerTickEvent evt) {
        if (evt.phase == TickEvent.Phase.END) {
            CreateEscalated.onServerTick(evt.getServer());
        }
    }

    private void onServerStopping(final ServerStoppingEvent evt) { CreateEscalated.onServerStopping(); }

}
