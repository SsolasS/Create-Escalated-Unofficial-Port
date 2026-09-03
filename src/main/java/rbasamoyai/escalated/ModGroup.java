package rbasamoyai.escalated;

import com.simibubi.create.Create;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import rbasamoyai.escalated.index.EscalatedItems;

public class ModGroup {

    public static final ResourceKey<CreativeModeTab> MAIN_TAB_KEY =
            makeKey("base");

    public static CreativeModeTab GROUP;

    public static void register() {
        if (GROUP != null)
            return;

        GROUP = CreativeModeTab.builder()
                .title(Component.translatable(
                        "itemGroup." + CreateEscalated.MOD_ID + ".base"))
                .icon(() -> new ItemStack(
                        EscalatedItems.METAL_WALKWAY_STEPS.get()))
                .displayItems((parameters, output) -> {
                    output.accept(EscalatedItems.METAL_WALKWAY_STEPS.get());
                    output.accept(EscalatedItems.WOODEN_WALKWAY_STEPS.get());
                })
                .build();

        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                MAIN_TAB_KEY,
                GROUP
        );

        CreateEscalated.REGISTRATE.addRawLang(
                "itemGroup." + CreateEscalated.MOD_ID + ".base",
                "Create: Escalated"
        );
    }

    public static void setDefaultTabToNull() {
        CreateEscalated.REGISTRATE.defaultCreativeTab(null);
    }

    public static ResourceKey<CreativeModeTab> makeKey(String id) {
        return ResourceKey.create(
                Registries.CREATIVE_MODE_TAB,
                CreateEscalated.resource(id)
        );
    }
}
