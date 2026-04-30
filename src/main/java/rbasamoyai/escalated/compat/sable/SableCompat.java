package rbasamoyai.escalated.compat.sable;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.escalated.walkways.WalkwayMovementHandler;

import java.util.Optional;

public class SableCompat {

    public static void register() {
        WalkwayMovementHandler.addWalkwayTransformer(new WalkwayTransformer());
    }

    private static class WalkwayTransformer implements WalkwayMovementHandler.WalkwayTransformer {
        public Optional<Vec3> transformPos(Entity entity, BlockEntity be) {
            SubLevel subLevel = Sable.HELPER.getContaining(be);
            return subLevel != null ? Optional.of(subLevel.logicalPose().transformPositionInverse(entity.position())) : Optional.empty();
        }

        @Override
        public Optional<Vec3> transformMovement(Vec3 movement, BlockEntity be) {
            SubLevel subLevel = Sable.HELPER.getContaining(be);
            return subLevel != null ? Optional.of(subLevel.logicalPose().transformNormal(movement)) : Optional.empty();
        }
    }

}
