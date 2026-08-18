package rbasamoyai.escalated.compat.sable;

import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import rbasamoyai.escalated.walkways.AbstractWalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwayBlockEntity;
import rbasamoyai.escalated.walkways.WalkwaySlope;

public class WalkwayBlockCallback implements BlockSubLevelCollisionCallback {

    public static final WalkwayBlockCallback INSTANCE = new WalkwayBlockCallback();

    private WalkwayBlockCallback() {}

    // Adapted from BeltBlockCallback#sable$onCollision --ritchie
    @Override
    public CollisionResult sable$onCollision(BlockPos hitBlockPos, @Nullable BlockPos otherHitBlockPos, Vector3d impactPosition, double impactVelocity) {
        SubLevelPhysicsSystem system = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
        ServerLevel level = system.getLevel();

        if (!(level.getBlockEntity(hitBlockPos) instanceof WalkwayBlockEntity walkwayBE))
            return CollisionResult.NONE;

        BlockState state = walkwayBE.getBlockState();
        if (!(state.getBlock() instanceof AbstractWalkwayBlock walkwayBlock))
            return CollisionResult.NONE;
        Direction facing = walkwayBlock.getFacing(state);
        WalkwaySlope slope = walkwayBlock.getWalkwaySlope(state);
        if (slope == WalkwaySlope.TERMINAL)
            return CollisionResult.NONE;

        Vec3i normal = Direction.get(Direction.AxisDirection.POSITIVE, facing.getAxis()).getNormal();
        float speed = walkwayBE.getWalkwayMovementSpeed() * 20.0f;

        if (facing.getAxis() == Direction.Axis.X) {
            speed *= -1.0f;
        }

        final Vector3d velocity = new Vector3d(normal.getX() * speed, normal.getY() * speed, normal.getZ() * speed);
        // TODO escalator motion?

        return new BlockSubLevelCollisionCallback.CollisionResult(velocity, false);
    }

}
