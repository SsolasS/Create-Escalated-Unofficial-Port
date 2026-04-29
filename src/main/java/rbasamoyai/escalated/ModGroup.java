package rbasamoyai.escalated;

import com.simibubi.create.Create;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.escalated.index.EscalatedItems;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModGroup {

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY = makeKey("base");

    private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateEscalated.MOD_ID);
    private static Map<ResourceKey<CreativeModeTab>, DeferredHolder<CreativeModeTab, CreativeModeTab>> TABS = new HashMap<>();

    public static final Supplier<CreativeModeTab> GROUP = wrapGroup("base", () -> createBuilder()
            .title(Component.translatable("itemGroup." + CreateEscalated.MOD_ID + ".base"))
            .icon(EscalatedItems.METAL_WALKWAY_STEPS::asStack)
            .displayItems((param, output) -> {
                output.acceptAll(Arrays.asList(
                        EscalatedItems.METAL_WALKWAY_STEPS.asStack(),
                        EscalatedItems.WOODEN_WALKWAY_STEPS.asStack()
                ));
            })
            .build());

    public static Supplier<CreativeModeTab> wrapGroup(String id, Supplier<CreativeModeTab> sup) {
        DeferredHolder<CreativeModeTab, CreativeModeTab> obj = TAB_REGISTER.register(id, sup);
        TABS.put(ModGroup.makeKey(id), obj);
        return obj;
    }

    public static CreativeModeTab.Builder createBuilder() {
        return CreativeModeTab.builder().withTabsBefore(Create.asResource("palettes"));
    }

    public static void registerNeoForge(IEventBus modBus) {
        TAB_REGISTER.register(modBus);
    }

    public static void setDefaultTabToNull() {
        CreateEscalated.REGISTRATE.defaultCreativeTab((ResourceKey<CreativeModeTab>) null);
    }

    public static ResourceKey<CreativeModeTab> makeKey(String id) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, CreateEscalated.resource(id));
    }

    public static void register() {
        CreateEscalated.REGISTRATE.addRawLang("itemGroup." + CreateEscalated.MOD_ID + ".base", "Create: Escalated");
    }

}
