package rbasamoyai.escalated.platform;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public class EnvExecute {

    public static void executeOnClient(Supplier<Runnable> run) {
        if (FMLEnvironment.dist == Dist.CLIENT)
            run.get().run();
    }

}
