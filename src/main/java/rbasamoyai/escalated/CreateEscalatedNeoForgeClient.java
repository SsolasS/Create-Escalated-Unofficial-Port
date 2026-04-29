package rbasamoyai.escalated;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import rbasamoyai.escalated.index.EscalatedBlockPartials;

import java.util.function.Supplier;

@Mod(value = CreateEscalated.MOD_ID, dist = Dist.CLIENT)
public class CreateEscalatedNeoForgeClient {

    public CreateEscalatedNeoForgeClient(IEventBus modBus) {
        IEventBus forgeBus = NeoForge.EVENT_BUS;

        EscalatedBlockPartials.init();

        modBus.addListener(CreateEscalatedNeoForgeClient::onClientSetup);
        modBus.addListener(CreateEscalatedNeoForgeClient::onLoadComplete);

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

    public static void onLoadComplete(FMLLoadCompleteEvent evt) {
        ModContainer container = ModList.get()
                .getModContainerById(CreateEscalated.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("Create: Escalated mod container missing on LoadComplete"));
        Supplier<IConfigScreenFactory> configScreen = () -> (mc, previousScreen) -> new BaseConfigScreen(previousScreen, CreateEscalated.MOD_ID);
        container.registerExtensionPoint(IConfigScreenFactory.class, configScreen);
    }

}
