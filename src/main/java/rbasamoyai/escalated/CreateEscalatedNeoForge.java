package rbasamoyai.escalated;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import rbasamoyai.escalated.compat.sable.SableCompat;
import rbasamoyai.escalated.config.EscalatedConfigs;
import rbasamoyai.escalated.index.EscalatedDataComponents;
import rbasamoyai.escalated.index.EscalatedTriggers;

@Mod(CreateEscalated.MOD_ID)
public class CreateEscalatedNeoForge {

    public CreateEscalatedNeoForge(IEventBus modBus) {
        IEventBus forgeBus = NeoForge.EVENT_BUS;
        ModLoadingContext mlContext = ModLoadingContext.get();

        CreateEscalated.REGISTRATE.registerEventListeners(modBus);

        CreateEscalated.init();
        ModGroup.registerNeoForge(modBus);

        EscalatedConfigs.registerConfigs(mlContext.getActiveContainer()::registerConfig);

        modBus.addListener(this::onLoadConfig);
        modBus.addListener(this::onReloadConfig);
        modBus.addListener(this::onRegister);

        forgeBus.addListener(this::onServerTick);
        forgeBus.addListener(this::onServerStopping);

        EscalatedModsNeoForge.SABLE.executeIfInstalled(() -> () -> SableCompat.register());
    }


    public void onRegister(final RegisterEvent event) {
        EscalatedDataComponents.register();
        if (event.getRegistry() == BuiltInRegistries.TRIGGER_TYPES) {
            EscalatedTriggers.register();
        }
    }

    private void onLoadConfig(ModConfigEvent.Loading evt) { EscalatedConfigs.onLoad(evt.getConfig()); }

    private void onReloadConfig(ModConfigEvent.Reloading evt) { EscalatedConfigs.onReload(evt.getConfig()); }

    private void onServerTick(final ServerTickEvent.Post evt) {
        CreateEscalated.onServerTick(evt.getServer());
    }

    private void onServerStopping(final ServerStoppingEvent evt) { CreateEscalated.onServerStopping(); }

}

