//? if <= 1.21.5 {
//#version 150
//#define LS_LOC(n)
//
//uniform mat4 TextureMat;
//?} else if <= 26.2 {
//#version 150
//#define LS_LOC(n)
//
//#moj_import <minecraft:dynamictransforms.glsl>
//?} else {
#version 330
#extension GL_ARB_separate_shader_objects : require
#define LS_LOC(n) layout(location = n)

#include <minecraft:projection.glsl>
#include <minecraft:fog.glsl>
#include <minecraft:dynamictransforms.glsl>
#include <minecraft:oit.glsl>

#ifdef LS_SCENE_DEPTH
uniform sampler2D Sampler1;

float lsGetSceneDistance(vec3 dir) {
    float depth = texelFetch(Sampler1, ivec2(gl_FragCoord.xy), 0).r;
    #ifndef RENDERPEARL_DEPTH_IS_ZERO_TO_ONE
    depth = depth * 2.0 - 1.0;
    #endif
    float denomenator = depth + ProjMat[2][2];
    float viewDepth = abs(denomenator) < 1.0e-7 ? 1.0e9 : ProjMat[3][2] / denomenator;
    if (viewDepth <= 0.0) viewDepth = 1.0e9;
    vec3 viewDir = (ModelViewMat * vec4(dir, 0.0)).xyz;
    return min(viewDepth * length(viewDir) / max(-viewDir.z, 1.0e-4), 1.0e9);
}
#endif
//?}

uniform sampler2D Sampler0;

LS_LOC(0) in vec3 relPos;
LS_LOC(1) in vec4 vertexColor;

#ifndef OIT_ALPHA_ONLY
LS_LOC(0) out vec4 fragColor;
#endif

const int LS_SAMPLES = 14;
const float LS_NOISE_SIZE = 128.0;
const float LS_COVER_LOW = 0.38; // Thin clouds below this
const float LS_COVER_HIGH = 0.64; // Dense clouds above
const float LS_THIN = 0.12; // Density between the clouds
const float LS_LIGHT_BASE = 0.4;
const float LS_LIGHT_VARIATION = 1.2;
const float LS_FADE_IN = 1.5;
const float LS_FADE_OUT_START = 24.0;
const float LS_FADE_OUT = 0.6;
const float LS_DETAIL_FADE_START = 16.0;
const float LS_DETAIL_FADE_END = 48.0;

vec2 lsCreateNoise(vec3 x) {
    vec3 cell = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    vec2 uv = cell.xy + vec2(37.0, 17.0) * cell.z + f.xy;
    vec4 slices = textureLod(Sampler0, (uv + 0.5) / LS_NOISE_SIZE, 0.0);
    return mix(slices.rb, slices.ga, f.z);
}

void main() {
    vec3 dir = normalize(relPos);
    float perBlock = TextureMat[0].w;
    float nearest = TextureMat[3].y;
    float furthest = TextureMat[3].z;
    float ratio = pow(furthest / nearest, 1.0 / float(LS_SAMPLES - 1));
    #ifdef LS_SCENE_DEPTH
    float sceneDistance = lsGetSceneDistance(dir);
    #else
    float sceneDistance = 1.0e9;
    #endif
    float distance = nearest;
    float transmittance = 1.0;
    float light = 0.0;
    for (int i = 0; i < LS_SAMPLES; i++) {
        float inner = i == 0 ? distance * 0.5 : distance * (1.0 + 1.0 /ratio) * 0.5;
        float outer = i == LS_SAMPLES - 1 ? distance * 1.15 : distance * (1.0 + ratio) * 0.5;
        if (inner >= sceneDistance) break;
        outer = min(outer, sceneDistance);
        float weight = smoothstep(0.5, LS_FADE_IN, distance) * (1.0 - LS_FADE_OUT * smoothstep(LS_FADE_OUT_START, furthest * 1.1, distance));
        vec3 relative = dir * distance;
        // Swirls behind the clouds
        vec2 swirl = lsCreateNoise(TextureMat[1].xyz + relative * (perBlock * 0.5));
        vec3 cloudPos = TextureMat[0].xyz + relative * perBlock + vec3(swirl.x - 0.5, swirl.y - 0.5, swirl.x - swirl.y) * TextureMat[1].w;
        vec2 large = lsCreateNoise(cloudPos);
        vec2 detail = lsCreateNoise(cloudPos * 2.0 + vec3(13.7,7.1,3.3));
        float fine = mix(detail.x, 0.5, smoothstep(LS_DETAIL_FADE_START, LS_DETAIL_FADE_END, distance));
        float cover = smoothstep(LS_COVER_LOW, LS_COVER_HIGH, large.x * 0.62 + fine * 0.38);
        float density = LS_THIN + (1.0 - LS_THIN) * cover;
        float alpha = 1.0 - exp(-density * TextureMat[3].w * (outer - inner) * weight);
        light += transmittance * alpha * (LS_LIGHT_BASE + LS_LIGHT_VARIATION * large.y);
        transmittance *= 1.0 - alpha;
        distance *= ratio;
    }
    float ahead = pow(max(dot(dir, TextureMat[2].xyz),0.0),8.0) * TextureMat[3].x;
    vec3 shade = vertexColor.rgb * light * (1.0 + ahead) * TextureMat[2].w;
    #ifdef LS_SCENE_DEPTH
    vec4 color = vec4(shade, 1.0 - transmittance);
    #else
    vec4 color = vec4(shade, 1.0);
    #endif
    #ifdef OIT_ALPHA_ONLY
    executeAlphaOnlyPhase(gl_FragCoord.z, color.a);
    #else
    #ifdef OIT_ACCUMULATE
    color = sampleColorForAccumulation(color);
    #endif
    fragColor = color;
    #endif
}
