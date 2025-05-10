package rbasamoyai.escalated.handrails;

import dev.engine_room.flywheel.api.instance.InstanceHandle;
import dev.engine_room.flywheel.api.instance.InstanceType;
import dev.engine_room.flywheel.lib.instance.OrientedInstance;
import net.createmod.catnip.render.SpriteShiftEntry;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class HandrailInstance extends OrientedInstance {

    public float sourceU;
    public float sourceV;
    public float minU;
    public float minV;
    public float maxU;
    public float maxV;
    public float scrollOffset;

    public HandrailInstance(InstanceType<? extends HandrailInstance> type, InstanceHandle handle) {
        super(type, handle);
    }

    public HandrailInstance setScrollTexture(SpriteShiftEntry spriteShift) {
        // Adapted from BeltData#setScrollTexture
        TextureAtlasSprite source = spriteShift.getOriginal();
        TextureAtlasSprite target = spriteShift.getTarget();

        this.sourceU = source.getU0();
        this.sourceV = source.getV0();
        this.minU = target.getU0();
        this.minV = target.getV0();
        this.maxU = target.getU1();
        this.maxV = target.getV1();

        return this;
    }

    public HandrailInstance setScrollOffset(float scrollOffset) {
        this.scrollOffset = scrollOffset;
        return this;
    }

}
