package rbasamoyai.escalated;

import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.LoadingModList;

import java.util.Optional;
import java.util.function.Supplier;

// Copied from Create's Mods class --ritchie
public enum EscalatedModsNeoForge {
    SABLE;

    private final String id;
    private final boolean isLoaded;

    EscalatedModsNeoForge() {
        this.id = CreateLang.asId(name());
        this.isLoaded = LoadingModList.get().getModFileById(this.id) != null;
    }

    public String id() { return this.id; }

    public ResourceLocation resource(String path) { return ResourceLocation.fromNamespaceAndPath(this.id, path); }

    public Block getBlock(String id) { return BuiltInRegistries.BLOCK.get(this.resource(id)); }

    public boolean isLoaded() { return this.isLoaded; }

    public <T> Optional<T> runIfInstalled(Supplier<Supplier<T>> toRun) {
        return this.isLoaded() ? Optional.of(toRun.get().get()) : Optional.empty();
    }

    public void executeIfInstalled(Supplier<Runnable> toExecute) { if (isLoaded()) toExecute.get().run(); }

}
