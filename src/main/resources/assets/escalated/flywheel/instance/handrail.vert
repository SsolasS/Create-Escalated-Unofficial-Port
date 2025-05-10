#include "flywheel:util/quaternion.glsl"

void flw_instanceVertex(in FlwInstance instance) {
    // Copied from oriented.vert
    flw_vertexPos = vec4(rotateByQuaternion(flw_vertexPos.xyz - instance.pivot, instance.rotation) + instance.pivot + instance.position, 1.0);
    flw_vertexNormal = rotateByQuaternion(flw_vertexNormal, instance.rotation);
    flw_vertexColor *= instance.color;
    flw_vertexOverlay = instance.overlay;
    // Some drivers have a bug where uint over float division is invalid, so use an explicit cast.
    flw_vertexLight = max(vec2(instance.light) / 256.0, flw_vertexLight);

    flw_vertexTexCoord = flw_vertexTexCoord - instance.sourceTexture + instance.scrollTexture.xy + vec2(0, instance.scroll);
}
