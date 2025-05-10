package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.model.Model;
import dev.engine_room.flywheel.api.visual.DynamicVisual;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static rbasamoyai.escalated.walkways.WalkwayRenderer.getStepOffset;

public class WalkwayVisual extends KineticBlockEntityVisual<WalkwayBlockEntity> implements SimpleDynamicVisual {

    private final RotatingInstance leftShaft;
    private final RotatingInstance rightShaft;
    private final RotatingInstance bottomShaft;
    private final OrientedInstance backStep;
    private final OrientedInstance frontStep;
    private DyeColor color;

    public WalkwayVisual(VisualizationContext ctx, WalkwayBlockEntity blockEntity, float partialTick) {
        super(ctx, blockEntity, partialTick);
        KineticBlock kinetic = (KineticBlock) this.blockState.getBlock();
        WalkwayBlock walkway = (WalkwayBlock) this.blockState.getBlock();
        Level level = this.blockEntity.getLevel();
        Direction left = Direction.fromAxisAndDirection(kinetic.getRotationAxis(this.blockState), Direction.AxisDirection.POSITIVE);
        Direction right = left.getOpposite();

        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, left)) {
            this.leftShaft = this.getShaftInstancer(left).createInstance();
        } else {
            this.leftShaft = this.getEmptyShaftInstancer().createInstance();
        }
        this.leftShaft.setPosition(this.getVisualPosition()).setup(blockEntity, left.getAxis());

        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, right)) {
            this.rightShaft = this.getShaftInstancer(right).createInstance();
        } else {
            this.rightShaft = this.getEmptyShaftInstancer().createInstance();
        }
        this.rightShaft.setPosition(this.getVisualPosition()).setup(blockEntity, right.getAxis());

        if (kinetic.hasShaftTowards(level, this.pos, this.blockState, Direction.DOWN)) {
            this.bottomShaft = this.getShaftInstancer(Direction.DOWN).createInstance();
        } else {
            this.bottomShaft = this.getEmptyShaftInstancer().createInstance();
        }
        this.bottomShaft.setPosition(this.getVisualPosition()).setup(blockEntity, Direction.Axis.Y);

        boolean isTerminal = walkway.getWalkwaySlope(this.blockState) == WalkwaySlope.TERMINAL;
        boolean isController = this.blockEntity.isController();
        Direction facing = this.getFacing();
        BlockPos pos1 = this.getVisualPosition();

        this.color = this.blockEntity.getColor();

        PartialModel model = this.getStepModel();
        if (isTerminal) {
            boolean flag = facing == Direction.NORTH || facing == Direction.EAST;
            facing = isController ? facing.getOpposite() : facing;
            if (flag) {
                this.backStep = this.getStepInstancer(Models.partial(model, facing)).createInstance();
                this.backStep.position(getStepOffset(this.blockEntity, facing, pos1, false)).setChanged();
                this.frontStep = this.getEmptyStepInstancer().createInstance();
            } else {
                this.frontStep = this.getStepInstancer(Models.partial(model, facing)).createInstance();
                this.frontStep.position(getStepOffset(this.blockEntity, facing, pos1, true)).setChanged();
                this.backStep = this.getEmptyStepInstancer().createInstance();
            }
        } else {
            this.frontStep = this.getStepInstancer(Models.partial(model, facing)).createInstance();
            this.frontStep.position(getStepOffset(this.blockEntity, facing, pos1, true)).setChanged();

            this.backStep = this.getStepInstancer(Models.partial(model, facing)).createInstance();
            this.backStep.position(getStepOffset(this.blockEntity, facing, pos1, false)).setChanged();
        }
    }

    private Instancer<RotatingInstance> getShaftInstancer(Direction dir) {
        return this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, dir));
    }

    private Instancer<RotatingInstance> getEmptyShaftInstancer() {
        return this.instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.block(Blocks.AIR.defaultBlockState()));
    }

    private Instancer<OrientedInstance> getStepInstancer(Model model) {
        return this.instancerProvider().instancer(InstanceTypes.ORIENTED, model);
    }

    private Instancer<OrientedInstance> getEmptyStepInstancer() {
        return this.instancerProvider().instancer(InstanceTypes.ORIENTED, Models.block(Blocks.AIR.defaultBlockState()));
    }

    private void updateSteps() {
        WalkwayBlock walkway = (WalkwayBlock) this.blockState.getBlock();
        boolean isTerminal = walkway.getWalkwaySlope(this.blockState) == WalkwaySlope.TERMINAL;
        boolean isController = this.blockEntity.isController();
        Direction facing = this.getFacing();

        PartialModel model = this.getStepModel();
        if (isTerminal) {
            boolean flag = facing == Direction.NORTH || facing == Direction.EAST;
            facing = isController ? facing.getOpposite() : facing;
            if (flag) {
                this.getStepInstancer(Models.partial(model, facing)).stealInstance(this.backStep);
                this.getEmptyStepInstancer().stealInstance(this.frontStep);
            } else {
                this.getStepInstancer(Models.partial(model, facing)).stealInstance(this.frontStep);
                this.getEmptyStepInstancer().stealInstance(this.backStep);
            }
        } else {
            this.getStepInstancer(Models.partial(model, facing)).stealInstance(this.frontStep);
            this.getStepInstancer(Models.partial(model, facing)).stealInstance(this.backStep);
        }
        this.frontStep.setChanged();
        this.backStep.setChanged();
    }

    @Override
    public void _delete() {
        this.leftShaft.delete();
        this.rightShaft.delete();
        this.bottomShaft.delete();
        this.backStep.delete();
        this.frontStep.delete();
    }

    protected Direction getFacing() {
        return ((WalkwayBlock) this.blockState.getBlock()).getFacing(this.blockState);
    }

    @Override
    public void beginFrame(DynamicVisual.Context ctx) {
        Direction facing = this.getFacing();
        BlockPos pos = this.getVisualPosition();
        this.frontStep.position(getStepOffset(this.blockEntity, facing, pos, true)).setChanged();
        this.backStep.position(getStepOffset(this.blockEntity, facing, pos, false)).setChanged();
    }

    @Override
    public void update(float partialTick) {
        super.update(partialTick);
        KineticBlock kinetic = (KineticBlock) this.blockState.getBlock();
        Direction.Axis axis = kinetic.getRotationAxis(this.blockState);

        if (this.blockEntity.getColor() != this.color || this.blockEntity.resetClientRender) {
            this.color = this.blockEntity.getColor();
            this.blockEntity.resetClientRender = false;
            this.updateSteps();
        }

        this.leftShaft.setup(this.blockEntity, axis).setChanged();
        this.rightShaft.setup(this.blockEntity, axis).setChanged();
        this.bottomShaft.setup(this.blockEntity, Direction.Axis.Y).setChanged();
    }

    @Override
    public void updateLight(float partialTick) {
        this.relight(this.pos, this.leftShaft);
        this.relight(this.pos, this.rightShaft);
        this.relight(this.pos, this.bottomShaft);
        this.relight(this.pos, this.backStep);
        this.relight(this.pos, this.frontStep);
    }

    /**
     * Override for your own mod's implementation of the walkway block entity.
     */
    protected PartialModel getStepModel() { return WalkwayRenderer.baseGetStepModel(this.blockEntity); }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        consumer.accept(this.leftShaft);
        consumer.accept(this.rightShaft);
        consumer.accept(this.bottomShaft);
        consumer.accept(this.frontStep);
        consumer.accept(this.backStep);
    }
}
