package rbasamoyai.escalated.index;

import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.api.layout.FloatRepr;
import dev.engine_room.flywheel.api.layout.IntegerRepr;
import dev.engine_room.flywheel.api.layout.LayoutBuilder;
import dev.engine_room.flywheel.lib.instance.SimpleInstanceType;
import dev.engine_room.flywheel.lib.util.ExtraMemoryOps;
import org.lwjgl.system.MemoryUtil;
import rbasamoyai.escalated.CreateEscalated;
import rbasamoyai.escalated.handrails.HandrailInstance;

public final class EscalatedInstanceTypes {

    public static final InstanceType<HandrailInstance> HANDRAIL = SimpleInstanceType.builder(HandrailInstance::new)
            .layout(LayoutBuilder.create()
                    .vector("color", FloatRepr.NORMALIZED_UNSIGNED_BYTE, 4)
                    .vector("overlay", IntegerRepr.SHORT, 2)
                    .vector("light", FloatRepr.UNSIGNED_SHORT, 2)
                    .vector("position", FloatRepr.FLOAT, 3)
                    .vector("pivot", FloatRepr.FLOAT, 3)
                    .vector("rotation", FloatRepr.FLOAT, 4)
                    .vector("sourceTexture", FloatRepr.FLOAT, 2)
                    .vector("scrollTexture", FloatRepr.FLOAT, 4)
                    .scalar("scroll", FloatRepr.FLOAT)
                    .build())
            .writer((ptr, instance) -> {
                MemoryUtil.memPutByte(ptr, instance.red);
                MemoryUtil.memPutByte(ptr + 1, instance.green);
                MemoryUtil.memPutByte(ptr + 2, instance.blue);
                MemoryUtil.memPutByte(ptr + 3, instance.alpha);
                ExtraMemoryOps.put2x16(ptr + 4, instance.overlay);
                ExtraMemoryOps.put2x16(ptr + 8, instance.light);
                MemoryUtil.memPutFloat(ptr + 12, instance.posX);
                MemoryUtil.memPutFloat(ptr + 16, instance.posY);
                MemoryUtil.memPutFloat(ptr + 20, instance.posZ);
                MemoryUtil.memPutFloat(ptr + 24, instance.pivotX);
                MemoryUtil.memPutFloat(ptr + 28, instance.pivotY);
                MemoryUtil.memPutFloat(ptr + 32, instance.pivotZ);
                ExtraMemoryOps.putQuaternionf(ptr + 36, instance.rotation);
                MemoryUtil.memPutFloat(ptr + 52, instance.sourceU);
                MemoryUtil.memPutFloat(ptr + 56, instance.sourceV);
                MemoryUtil.memPutFloat(ptr + 60, instance.minU);
                MemoryUtil.memPutFloat(ptr + 64, instance.minV);
                MemoryUtil.memPutFloat(ptr + 68, instance.maxU);
                MemoryUtil.memPutFloat(ptr + 72, instance.maxV);
                MemoryUtil.memPutFloat(ptr + 76, instance.scrollOffset);
            })
            .vertexShader(CreateEscalated.resource("instance/handrail.vert"))
            .cullShader(CreateEscalated.resource("instance/cull/handrail.glsl"))
            .build();

    public static void init() {}

}
