package rbasamoyai.escalated.index;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import rbasamoyai.escalated.CreateEscalated;

import java.util.function.UnaryOperator;

public class EscalatedDataComponents {
    public static final DataComponentType<BlockPos> WALKWAY_FIRST_TERMINAL = register(
            "walkway_first_terminal",
            builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CreateEscalated.resource(name), builder.apply(DataComponentType.builder()).build());
    }

    public static void register() {}
}
