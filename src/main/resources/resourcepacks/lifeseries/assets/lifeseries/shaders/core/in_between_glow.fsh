//? if <= 26.2 {
//#version 150
//#define LS_LOC(n)
//?} else {
#version 330
#extension GL_ARB_separate_shader_objects : require
#define LS_LOC(n) layout(location = n)

#include <minecraft:oit.glsl>
//?}

uniform sampler2D Sampler0;

LS_LOC(0) in vec2 texCoord0;
LS_LOC(1) in vec4 vertexColor;
LS_LOC(2) in float fogFade;

#ifndef OIT_ALPHA_ONLY
LS_LOC(0) out vec4 fragColor;
#endif

const float LS_ENERGY_MAX = 24.0;

// In-Between particle glow
void main() {
    #if defined(OIT_ALPHA_ONLY) && defined(OIT_ADDITIVE)
    discard; // Added light doesn't hide anything behind it
    #endif
    vec4 sprite = texture(Sampler0, texCoord0);
    vec3 glow;
    if (sprite.a > 0.0) {
        glow = 1.0 - exp(-sprite.rgb * sprite.rgb * LS_ENERGY_MAX * vertexColor.a);
    }
    else {
        glow = sprite.rgb * vertexColor.rgb * vertexColor.a;
    }
    glow *= fogFade;
    if (max(glow.r, max(glow.g, glow.b)) <= 0.0) {
        discard;
    }
    vec4 color = vec4(glow, 1.0);
    #ifdef OIT_ALPHA_ONLY
    executeAlphaOnlyPhase(gl_FragCoord.z, color.a);
    #else
    #ifdef OIT_ACCUMULATE
    color = sampleColorForAccumulation(color);
    #endif
    fragColor = color;
    #endif
}
