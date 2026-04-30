package rbasamoyai.escalated.mixin.compat.sable;

import dev.ryanhcode.sable.api.block.BlockWithSubLevelCollisionCallback;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import org.spongepowered.asm.mixin.Mixin;
import rbasamoyai.escalated.compat.sable.WalkwayBlockCallback;
import rbasamoyai.escalated.walkways.AbstractWalkwayBlock;

@Mixin(AbstractWalkwayBlock.class)
public class AbstractWalkwayBlockMixin implements BlockWithSubLevelCollisionCallback {
    @Override public BlockSubLevelCollisionCallback sable$getCallback() { return WalkwayBlockCallback.INSTANCE; }
}
