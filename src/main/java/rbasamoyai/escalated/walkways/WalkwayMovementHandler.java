package rbasamoyai.escalated.walkways;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import rbasamoyai.escalated.advancements.WalkwayTravelTracker;

import java.util.List;
import java.util.Optional;

import static net.minecraft.core.Direction.AxisDirection.NEGATIVE;
import static net.minecraft.core.Direction.AxisDirection.POSITIVE;
import static net.minecraft.world.entity.MoverType.SELF;

/**
 * Adapted from {@link com.simibubi.create.content.kinetics.belt.transport.BeltMovementHandler}
 */
public class WalkwayMovementHandler {

    public static class TransportedEntityInfo {
        int ticksSinceLastCollision;
        BlockPos lastCollidedPos;
        BlockState lastCollidedState;

        public TransportedEntityInfo(BlockPos collision, BlockState walkway) { this.refresh(collision, walkway); }

        public void refresh(BlockPos collision, BlockState walkway) {
            this.ticksSinceLastCollision = 0;
            this.lastCollidedPos = new BlockPos(collision).immutable();
            this.lastCollidedState = walkway;
        }

        public TransportedEntityInfo tick() {
            this.ticksSinceLastCollision++;
            return this;
        }

        public int getTicksSinceLastCollision() { return this.ticksSinceLastCollision; }
    }

    public static boolean canBeTransported(Entity entity) {
        return entity.isAlive() && (!(entity instanceof Player player) || !player.isShiftKeyDown());
    }

    public static void transportEntity(WalkwayBlockEntity walkwayBE, Entity entity, TransportedEntityInfo info) {
        BlockPos pos = info.lastCollidedPos;
        Level level = walkwayBE.getLevel();
        BlockEntity otherBE = level.getBlockEntity(pos);

        BlockPos belowPos = BlockPos.containing(entity.position().subtract(0, 0.05, 0));
        BlockEntity beBelowPassenger = level.getBlockEntity(belowPos);
        BlockState blockState = info.lastCollidedState;
        WalkwayBlock walkwayBlock = (WalkwayBlock) blockState.getBlock();
        Direction movementFacing = Direction.fromAxisAndDirection(walkwayBlock.getFacing(blockState).getAxis(),
                walkwayBE.getSpeed() < 0 ? POSITIVE : NEGATIVE);

        boolean collidedWithWalkways = otherBE instanceof WalkwayBlockEntity;
        boolean betweenWalkways = beBelowPassenger instanceof WalkwayBlockEntity && beBelowPassenger != otherBE;

        // Don't fight other walkways
        if (!collidedWithWalkways || betweenWalkways)
            return;

        // Too slow or doesn't move (e.g. terminals)
        if (Math.abs(walkwayBE.getSpeed()) < 1 || !walkwayBlock.movesEntities(blockState))
            return;

        Vec3 entityPos = getEntityPos(entity, walkwayBE);
        double entityY = entityPos.y;
        // Not on top
        if (entityY + 0.25d < pos.getY())
            return;

        // Lock entities in place
        boolean isPlayer = entity instanceof Player;
        if (entity instanceof LivingEntity living && !isPlayer)
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 1, false, false));

        final Direction walkwayFacing = walkwayBlock.getFacing(blockState);
        final WalkwaySlope slope = walkwayBlock.getWalkwaySlope(blockState);
        final Direction.Axis axis = walkwayFacing.getAxis();
        float movementSpeed = walkwayBE.getWalkwayMovementSpeed();
        final Direction movementDirection = Direction.get(axis == Direction.Axis.X ? NEGATIVE : POSITIVE, axis);

        Vec3i centeringDirection = Direction.get(POSITIVE, walkwayFacing.getClockWise().getAxis()).getNormal();
        Vec3 movement = Vec3.atLowerCornerOf(movementDirection.getNormal()).scale(movementSpeed);

        double diffCenter = axis == Direction.Axis.Z ? (pos.getX() + .5d - entityPos.x) : (pos.getZ() + .5d - entityPos.z);
        if (Math.abs(diffCenter) > 48 / 64d)
            return;

        double top = 15.5d / 16d;
        boolean onSlope = slope == WalkwaySlope.MIDDLE || slope == WalkwaySlope.TOP && entityY - pos.getY() < top
                || slope == WalkwaySlope.BOTTOM && entityY - pos.getY() > top;

        boolean movingDown = onSlope && movementFacing != walkwayFacing;
        boolean movingUp = onSlope && movementFacing == walkwayFacing;

        if (walkwayFacing.getAxis() == Direction.Axis.Z) {
            boolean b = movingDown;
            movingDown = movingUp;
            movingUp = b;
        }

        if (movingUp)
            movement = movement.add(0, Math.abs(axis.choose(movement.x, movement.y, movement.z)), 0);
        if (movingDown)
            movement = movement.add(0, -Math.abs(axis.choose(movement.x, movement.y, movement.z)), 0);

        Vec3 centering = Vec3.atLowerCornerOf(centeringDirection).scale(diffCenter * Math.min(Math.abs(movementSpeed), .1d) * 4);

        if (!(entity instanceof LivingEntity living) || living.zza == 0 && living.xxa == 0)
            movement = movement.add(centering);

        float step = entity.maxUpStep();
        if (!isPlayer && entity instanceof LivingEntity livingEntity) {
            step = (float) livingEntity.getAttributeBaseValue(Attributes.STEP_HEIGHT);
            //noinspection DataFlowIssue
            livingEntity.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(1.0f);
        }

        // Entity Collisions
        if (Math.abs(movementSpeed) < .5d) {
            Vec3 checkDistance = movement.normalize()
                    .scale(0.5);
            AABB bb = entity.getBoundingBox();
            AABB checkBB = new AABB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            checkBB = checkBB.move(checkDistance)
                    .inflate(-Math.abs(checkDistance.x), -Math.abs(checkDistance.y), -Math.abs(checkDistance.z));
            List<Entity> list = level.getEntities(entity, checkBB);
            list.removeIf(e -> shouldIgnoreBlocking(entity, e));
            if (!list.isEmpty()) {
                entity.setDeltaMovement(0, 0, 0);
                info.ticksSinceLastCollision--;
                return;
            }
        }

        entity.fallDistance = 0;

        if (isPlayer && hasMovementInput(entity) && (movingUp || movingDown)) {
            applyPlayerInputCombinedMovement(entity, transformMovement(movement, walkwayBE), movingUp, movingDown, movementSpeed);
            if (!entity.level().isClientSide)
                WalkwayTravelTracker.trackPlayerOnWalkway((Player) entity, 300); // 15 seconds
            return;
        }

        if (movingUp) {
            float minVelocity = .13f;
            float yMovement = (float) -Math.max(Math.abs(movement.y), minVelocity);
            entity.move(SELF, transformMovement(new Vec3(0, yMovement, 0), walkwayBE));
            entity.move(SELF, transformMovement(movement.multiply(1, 0, 1), walkwayBE));
        } else if (movingDown) {
            entity.move(SELF, transformMovement(movement.multiply(1, 0, 1), walkwayBE));
            entity.move(SELF, transformMovement(movement.multiply(0, 1, 0), walkwayBE));
        } else {
            entity.move(SELF, transformMovement(movement, walkwayBE));
        }

        // Placement on steps
        entity.setOnGround(true);

        if (isPlayer && !entity.level().isClientSide) {
            WalkwayTravelTracker.trackPlayerOnWalkway((Player) entity, 300); // 15 seconds
        }
        if (!isPlayer && entity instanceof LivingEntity livingEntity) {
            livingEntity.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(step);
        }

        boolean movedPastEndingSlope = onSlope && (isWalkway(level, entity.blockPosition()) || isWalkway(level, entity.blockPosition().below()));

        if (movedPastEndingSlope && !movingDown && Math.abs(movementSpeed) > 0)
            entity.setPos(entity.getX(), entity.getY() + movement.y, entity.getZ());
        if (movedPastEndingSlope) {
            entity.setDeltaMovement(transformMovement(movement, walkwayBE));
            entity.hurtMarked = true;
        }
    }

    private static boolean isWalkway(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof WalkwayBlock;
    }

    private static boolean hasMovementInput(Entity entity) {
        return entity instanceof LivingEntity living && (living.zza != 0 || living.xxa != 0);
    }

    private static void applyPlayerInputCombinedMovement(Entity entity, Vec3 movement, boolean movingUp, boolean movingDown, float movementSpeed) {
        Vec3 delta = entity.getDeltaMovement();
        Vec3 horizontalAssist = movement.multiply(1, 0, 1);
        Vec3 input = getWorldInput(entity);
        double dot = input.lengthSqr() < 1.0E-7 || horizontalAssist.lengthSqr() < 1.0E-7
                ? 0
                : input.normalize().dot(horizontalAssist.normalize());
        double scale = dot > 0.25 ? 0.2 : dot < -0.25 ? 0.55 : 0.35;
        Vec3 combined = delta;
        if (horizontalAssist.lengthSqr() > 1.0E-7) {
            Vec3 assistDirection = horizontalAssist.normalize();
            double targetAlongAssist = horizontalAssist.length() * scale;
            double currentAlongAssist = delta.dot(assistDirection);
            double addAlongAssist = targetAlongAssist - currentAlongAssist;
            addAlongAssist = Math.max(-targetAlongAssist, Math.min(targetAlongAssist, addAlongAssist));
            combined = combined.add(assistDirection.scale(addAlongAssist));
        }

        double verticalAssist = Math.min(Math.max(Math.abs(movement.y) * 0.75, Math.abs(movementSpeed) * 0.5), 0.12);
        if (movingUp)
            combined = new Vec3(combined.x, Math.max(delta.y, verticalAssist), combined.z);
        if (movingDown)
            combined = new Vec3(combined.x, Math.min(delta.y, -verticalAssist), combined.z);

        entity.setDeltaMovement(combined);
        entity.setOnGround(true);
        entity.hurtMarked = true;
    }

    private static Vec3 getWorldInput(Entity entity) {
        if (!(entity instanceof LivingEntity living))
            return Vec3.ZERO;
        double x = living.xxa;
        double z = living.zza;
        double lengthSqr = x * x + z * z;
        if (lengthSqr < 1.0E-7)
            return Vec3.ZERO;
        if (lengthSqr > 1) {
            double length = Math.sqrt(lengthSqr);
            x /= length;
            z /= length;
        }
        double yaw = Math.toRadians(entity.getYRot());
        double sin = Math.sin(yaw);
        double cos = Math.cos(yaw);
        return new Vec3(x * cos - z * sin, 0, z * cos + x * sin);
    }

    public static boolean shouldIgnoreBlocking(Entity me, Entity other) {
        if (other instanceof HangingEntity)
            return true;
        if (other.getPistonPushReaction() == PushReaction.IGNORE)
            return true;
        return isRidingOrBeingRiddenBy(me, other);
    }

    public static boolean isRidingOrBeingRiddenBy(Entity me, Entity other) {
        for (Entity entity : me.getPassengers()) {
            if (entity.equals(other))
                return true;
            if (isRidingOrBeingRiddenBy(entity, other))
                return true;
        }
        return false;
    }

    // Compatibility code //

    private static final List<WalkwayTransformer> TRANSFORMERS = new ReferenceArrayList<>();

    public static void addWalkwayTransformer(WalkwayTransformer t) { TRANSFORMERS.add(t); }

    public static Vec3 getEntityPos(Entity entity, BlockEntity be) {
        for (WalkwayTransformer t : TRANSFORMERS) {
            Optional<Vec3> o = t.transformPos(entity, be);
            if (o.isPresent())
                return o.get();
        }
        return entity.position();
    }

    public static Vec3 transformMovement(Vec3 movement, BlockEntity be) {
        for (WalkwayTransformer t : TRANSFORMERS) {
            Optional<Vec3> o = t.transformMovement(movement, be);
            if (o.isPresent())
                return o.get();
        }
        return movement;
    }

    public interface WalkwayTransformer {
        Optional<Vec3> transformPos(Entity entity, BlockEntity be);
        Optional<Vec3> transformMovement(Vec3 movement, BlockEntity be);
    }

}
