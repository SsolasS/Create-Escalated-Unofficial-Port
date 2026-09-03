package rbasamoyai.escalated.platform;

import net.fabricmc.loader.api.FabricLoader;

import java.util.function.Supplier;

public class EnvExecute {

    public static void executeOnClient(Supplier<Runnable> run) {
        if (FabricLoader.getInstance().getEnvironmentType().isClient()) {
            run.get().run();
        }
    }
}
