//? if <= 1.21.5 {
//#version 150
//#define LS_LOC(n)
//
//uniform mat4 TextureMat;
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
//?} else {
#version 330
#extension GL_ARB_separate_shader_objects : require
#define LS_LOC(n) layout(location = n)

#include <minecraft:fog.glsl>
#include <minecraft:dynamictransforms.glsl>
#include <minecraft:oit.glsl>
//?}

uniform sampler2D Sampler0;

LS_LOC(0) in vec3 relPos;
LS_LOC(1) in vec4 vertexColor;
LS_LOC(2) in vec3 eye;

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

const int LS_STEPS = 16;
const float LS_NOISE_SIZE = 128.0;
const float LS_SOURCE_FADE = 10.0;
const float LS_DUST = 0.35;

vec2 lsCreateNoise(vec3 x) {
    vec3 cell = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    vec2 uv = cell.xy + vec2(37.0, 17.0) * cell.z + f.xy;
    vec4 slices = textureLod(Sampler0, (uv + 0.5) / LS_NOISE_SIZE, 0.0);
    return mix(slices.rb, slices.ga, f.z);
}

void lsKeepAbove(vec3 normal, float limit, vec3 origin, vec3 dir, inout float tEnter, inout float tExit) {
    float speed = dot(normal, dir);
    float room = limit - dot(normal, origin);
    if (abs(speed) <0.000001) {
        if (room > 0.0) tExit = -1.0;
        return;
    }
    float t = room / speed;
    if (speed > 0.0) tEnter = max(tEnter, t);
    else tExit = min(tExit, t);
}

void main() {
    #if defined(OIT_ALPHA_ONLY) && defined(OIT_ADDITIVE)
    discard;
    #endif
    vec3 source = TextureMat[0].xyz;
    float sourceRadius = TextureMat[0].w;
    vec3 axis = TextureMat[1].xyz;
    float spread = TextureMat[1].w;
    float floorY = TextureMat[2].x;
    float density = TextureMat[2].y;
    vec3 dustOffset = vec3(TextureMat[2].zw, TextureMat[3].x);
    float dustScale = TextureMat[3].y;

    vec3 dir = normalize(relPos - eye);
    float tipDistance = sourceRadius / spread;
    vec3 fromTip = eye - (source - axis * tipDistance);
    float widening = 1.0 + spread * spread;
    float along = dot(dir, axis);
    float tipAlong = dot(fromTip, axis);
    float a = 1.0 - widening * along * along;
    float b = dot(fromTip, dir) - widening * tipAlong * along;
    float c = dot(fromTip, fromTip) - widening * tipAlong * tipAlong;
    if (abs(a) < 0.00001) a = a < 0.0 ? -0.00001 : 0.00001;
    float diskriminant = b * b - a * c;
    float tEnter = 0.0;
    float tExit = lsFogEnd();
    if (diskriminant >0.0) {
        float root = sqrt(diskriminant);
        float low = min((-b - root) / a, (-b + root) / a);
        float high = max((-b - root) / a, (-b + root) / a);
        if (a > 0.0) {
            tEnter = max(tEnter, low);
            tExit = min(tExit, high);
        }
        else if (along > 0.0) {
            tEnter = max(tEnter, high);
        }
        else {
            tExit = min(tExit, low);
        }
    }
    else if (a > 0.0) {
        discard;
    }
    lsKeepAbove(axis, dot(source, axis), eye, dir, tEnter, tExit);
    lsKeepAbove(vec3(0.0, 1.0, 0.0), floorY, eye, dir, tEnter, tExit);
    if (tExit <= tEnter) {
        discard;
    }

    float cylindrical = lsCylindrical(dir);
    float stepLength = (tExit - tEnter) / float(LS_STEPS);
    float jitter = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    float light = 0.0;
    for (int i = 0; i < LS_STEPS; i++) {
        float t = tEnter + (float(i) + jitter) * stepLength;
        vec3 pos = eye + dir * t;
        vec3 fromSource = pos - source;
        float depth = dot(fromSource, axis);
        float across = length(fromSource - axis * depth) / (sourceRadius + spread * depth);
        float profile = max(1.0 - across * across, 0.0);
        float dust = 1.0 + LS_DUST * (2.0*lsCreateNoise(pos * dustScale + dustOffset).x - 1.0);
        light += profile * profile * dust * smoothstep(0.0, LS_SOURCE_FADE, depth) * (1.0 - lsFogAt(t, cylindrical));
    }
    vec3 glow = vertexColor.rgb * (1.0 - exp(-light * stepLength * density)) * vertexColor.a;
    glow = max(glow + (jitter - 0.5) / 255.0, vec3(0.0));
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
