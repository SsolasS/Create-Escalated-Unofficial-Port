package rbasamoyai.escalated.datagen;

import com.tterrag.registrate.providers.ProviderType;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.index.EscalatedPonderPlugin;

import java.util.function.BiConsumer;

@EventBusSubscriber(modid = CreateEscalated.MOD_ID)
public class EscalatedDataGeneration {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onGatherRegistrateData(GatherDataEvent evt) {
        if (!evt.getMods().contains(CreateEscalated.MOD_ID))
            return;
        CreateEscalated.REGISTRATE.addDataGenerator(ProviderType.LANG, prov -> {
            providePonderLang(prov::add);
        });
        EscalatedLangGen.prepare();
        EscalatedCraftingRecipeProvider.register();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGatherData(GatherDataEvent evt) {
        if (!evt.getMods().contains(CreateEscalated.MOD_ID))
            return;

        ExistingFileHelper helper = evt.getExistingFileHelper();

        DataGenerator generator = evt.getGenerator();
        PackOutput output = generator.getPackOutput();

        generator.addProvider(evt.includeClient(), new EscalatedPartialsGen(output, CreateEscalated.MOD_ID, helper));
    }

    private static void providePonderLang(BiConsumer<String, String> cons) {
        PonderIndex.addPlugin(new EscalatedPonderPlugin());
        PonderIndex.getLangAccess().provideLang(CreateEscalated.MOD_ID, cons);
    }

}
