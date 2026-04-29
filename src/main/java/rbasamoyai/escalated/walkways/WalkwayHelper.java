package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;

/**
 * Adapted from {@link com.simibubi.create.content.kinetics.belt.BeltHelper}
 */
public class WalkwayHelper {

    public static WalkwayBlockEntity getSegmentBE(LevelAccessor level, BlockPos pos) {
        if (level instanceof Level l && !l.isLoaded(pos))
            return null;
        return level.getBlockEntity(pos) instanceof WalkwayBlockEntity walkway ? walkway : null;
    }

    public static WalkwayBlockEntity getControllerBE(LevelAccessor level, BlockPos pos) {
        WalkwayBlockEntity segment = getSegmentBE(level, pos);
        if (segment == null)
            return null;
        BlockPos controllerPos = segment.controller;
        return controllerPos == null ? null : getSegmentBE(level, controllerPos);
    }

    public static boolean isDye(ItemStack itemStack) { return itemStack.is(Tags.Items.DYES); }

    public static boolean hasWater(Level level, ItemStack itemStack) {
        return GenericItemEmptying.emptyItem(level, itemStack, true).getFirst().getFluid().isSame(Fluids.WATER);
    }

    public static DyeColor getDyeColorFromItem(ItemStack itemStack) { return DyeColor.getColor(itemStack); }

    public static boolean isHandrail(ItemStack itemStack) {
        return AllItems.BELT_CONNECTOR.isIn(itemStack);
    }

    private WalkwayHelper() {}

}
