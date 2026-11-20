//? if <= 1.21.5 {
//#version 150
//#define LS_LOC(n)
//
//uniform mat4 TextureMat;
//uniform float GameTime;
//uniform float FogStart;
//uniform float FogEnd;
//
//float lsFogAt(float t, float cylindrical) {
//    return clamp((t - FogStart) / max(FogEnd - FogStart, 0.001), 0.0, 1.0);
//}
//
//float lsCylindrical(vec3 dir) {
//    return 1.0;
//}
//
//float lsFogEnd() {
//    return FogEnd > FogStart ? FogEnd : 100000.0;
//}
//?} else if <= 26.2 {
//#version 150
//#define LS_LOC(n)
//
//#moj_import <minecraft:fog.glsl>
//#moj_import <minecraft:dynamictransforms.glsl>
//#moj_import <minecraft:globals.glsl>
//?} else {
#version 330
#extension GL_ARB_separate_shader_objects : require
#define LS_LOC(n) layout(location = n)

#include <minecraft:fog.glsl>
#include <minecraft:dynamictransforms.glsl>
#include <minecraft:globals.glsl>
#include <minecraft:oit.glsl>
//?}

uniform sampler2D Sampler0;

LS_LOC(0) in vec3 localPos;
LS_LOC(1) in vec3 relPos;
LS_LOC(2) in vec4 vertexColor;
LS_LOC(3) in vec3 eyeLocal;

#ifndef OIT_ALPHA_ONLY
LS_LOC(0) out vec4 fragColor;
#endif

//? if >= 1.21.6 {
float lsFogAt(float t, float cylindrical) {
    return total_fog_value(t, t * cylindrical, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd);
}

float lsCylindrical(vec3 dir) {
    return fog_cylindrical_distance(dir);
}

float lsFogEnd() {
    return FogEnvironmentalEnd > FogEnvironmentalStart ? FogEnvironmentalEnd : 100000.0;
}
//?}

const int LS_RAY_STEPS = 18;
const float LS_TAU = 6.2831853;
const float LS_LIGHT = 0.38;
const float LS_COVER = 0.14;
const float LS_COVER_NEAR = 1.0;
const float LS_COVER_FAR = 6.0;
const float LS_SATURATION = 1.5;
const float LS_HAZE = 0.22;
const float LS_OPENING_BOOST = 1.5;
const float LS_OPENING_DEPTH = 1.0;
const float LS_BURST = 1.6;
const float LS_BURST_SPACING = 8.0;
const float LS_BURST_CYCLES = 300.0;
const float LS_BURST_MEAN = 0.156;

vec3 lsSaturate(vec3 color) {
    float luminance = dot(color, vec3(0.2126, 0.7152, 0.0722));
    return clamp(mix(vec3(luminance), color, LS_SATURATION), 0.0, 1.0);
}

void lsClip(vec3 normal, float limit, vec3 origin, vec3 dir, inout float tEnter, inout float tExit) {
    float along = dot(normal, dir);
    float room = limit - dot(normal, origin);
    if (abs(along) < 0.000001) {
        if (room < 0.0) tExit = -1.0;
        return;
    }
    float t = room / along;
    if (along < 0.0) tEnter = max(tEnter, t);
    else tExit = min(tExit, t);
}

void main() {
    float halfWidth = TextureMat[1].y;
    float halfHeight = TextureMat[1].z;
    float sourceDepth = TextureMat[1].w;
    float rayLength = TextureMat[2].x;
    float fadeStart = TextureMat[2].y;

    vec3 origin = eyeLocal;
    vec3 dir = normalize(localPos - origin);
    float cylindrical = lsCylindrical(normalize(relPos));

    float tEnter = 0.0;
    float tExit = 100000.0;
    lsClip(vec3(0.0, 0.0, -1.0), 0.0, origin, dir, tEnter, tExit);
    lsClip(vec3(0.0, 0.0, 1.0), rayLength, origin, dir, tEnter, tExit);
    lsClip(vec3(1.0, 0.0, -halfWidth / sourceDepth), halfWidth, origin, dir, tEnter, tExit);
    lsClip(vec3(-1.0, 0.0, -halfWidth / sourceDepth), halfWidth, origin, dir, tEnter, tExit);
    lsClip(vec3(0.0, 1.0, -halfHeight / sourceDepth), halfHeight, origin, dir, tEnter, tExit);
    lsClip(vec3(0.0, -1.0, -halfHeight / sourceDepth), halfHeight, origin, dir, tEnter, tExit);
    tExit = min(tExit, lsFogEnd());
    if (tExit <= tEnter) {
        discard;
    }

    float burstTime = GameTime * LS_TAU * LS_BURST_CYCLES;
    float haze = mix(LS_HAZE, 1.0, TextureMat[2].z);
    float stepLenght = (tExit - tEnter) / float(LS_RAY_STEPS);
    float jitter = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056,0.00583715))));
    vec3 light = vec3(0.0);
    float transmittance = 1.0;
    for (int i = 0; i < LS_RAY_STEPS; i++) {
        float t = tEnter + (float(i) + jitter) * stepLenght;
        vec3 pos = origin + dir * t;
        float z = max(pos.z, 0.0);
        float spread = sourceDepth / (z + sourceDepth);
        vec2 windowPos = pos.xy * spread;
        vec4 ray = textureLod(Sampler0, vec2(windowPos.x / (2.0*halfWidth) + 0.5, 0.5 - windowPos.y / (2.0* halfHeight)),0.0);
        float fade = (1.0 - smoothstep(fadeStart, rayLength, z)) * spread * (1.0 + LS_OPENING_BOOST * exp(-z / LS_OPENING_DEPTH));
        float density = ray.a * fade * (1.0 - lsFogAt(t, cylindrical));
        float phase = ray.a * 12.0 + dot(windowPos, vec2(1.3, -0.9));
        float wave = max(sin(z * (LS_TAU / LS_BURST_SPACING) - burstTime + phase),0.0);
        float wave2 = wave * wave;
        float pulse = 1.0 + LS_BURST * (wave2 * wave2 * wave2 - LS_BURST_MEAN);
        float along = dot(dir, vec3(pos.xy, z + sourceDepth)) * inversesqrt(dot(pos.xy, pos.xy) + (z + sourceDepth) * (z + sourceDepth));
        float across = sqrt(max(1.0 - along * along, 0.0));
        light += ray.rgb * (density * pulse * mix(haze, 1.0, across * sqrt(across)) * transmittance);
        transmittance *= max(1.0 - density * LS_COVER * stepLenght * smoothstep(LS_COVER_NEAR, LS_COVER_FAR, t),0.0);
        if (transmittance < 0.02) break;
    }
    light = lsSaturate(light * stepLenght * LS_LIGHT) * vertexColor.a;
    float cover = (1.0 - transmittance) * vertexColor.a;
    light = max(light + (jitter - 0.5) / 255.0, vec3(0.0));
    if (max(light.r, max(light.g, light.b)) < 0.002 && cover < 0.002) {
        discard;
    }

    #ifdef OIT_ALPHA_ONLY
    executeAlphaOnlyPhase(gl_FragCoord.z, cover);
    #else
    vec4 color = vec4(light, cover);
    #ifdef OIT_ACCUMULATE
    color = sampleColorForAccumulation(vec4(light / max(cover, 0.001), cover));
    #endif
    fragColor = color;
    #endif
}
