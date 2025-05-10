package rbasamoyai.escalated;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import rbasamoyai.escalated.index.EscalatedBlockPartials;
import rbasamoyai.escalated.index.EscalatedInstanceTypes;
import rbasamoyai.escalated.index.EscalatedPonderPlugin;
import rbasamoyai.escalated.index.EscalatedSpriteShiftEntries;
import rbasamoyai.escalated.walkways.WalkwayConnectorHandler;

public class EscalatedClientCommon {

    public static void onClientSetup() {
        EscalatedInstanceTypes.init();
        EscalatedSpriteShiftEntries.init();
        EscalatedBlockPartials.resolveDeferredModels();
        PonderIndex.addPlugin(new EscalatedPonderPlugin());
    }

    public static void onClientGameTick(Minecraft minecraft) {
        if (minecraft.level == null || minecraft.player == null)
            return;

        WalkwayConnectorHandler.tick();
    }

}
