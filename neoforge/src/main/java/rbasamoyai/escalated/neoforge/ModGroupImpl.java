package rbasamoyai.escalated.neoforge;

import com.simibubi.create.Create;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.ModGroup;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModGroupImpl {

	private static final DeferredRegister<CreativeModeTab> TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateEscalated.MOD_ID);
	private static Map<ResourceKey<CreativeModeTab>, DeferredHolder<CreativeModeTab, CreativeModeTab>> TABS = new HashMap<>();

	public static Supplier<CreativeModeTab> wrapGroup(String id, Supplier<CreativeModeTab> sup) {
		DeferredHolder<CreativeModeTab, CreativeModeTab> obj = TAB_REGISTER.register(id, sup);
		TABS.put(ModGroup.makeKey(id), obj);
		return obj;
	}

	public static CreativeModeTab.Builder createBuilder() {
		return CreativeModeTab.builder().withTabsBefore(Create.asResource("palettes"));
	}

	public static void registerForge(IEventBus modBus) {
		TAB_REGISTER.register(modBus);
	}

	public static void useModTab(ResourceKey<CreativeModeTab> key) {
		CreateEscalated.REGISTRATE.setCreativeTab(TABS.get(key));
	}

}
