//? if <= 1.21.5 {
//#version 150
//#define LS_LOC(n)
//
//uniform mat4 ModelViewMat;
//uniform mat4 ProjMat;
//uniform float FogStart;
//uniform float FogEnd;
//
//float lsFogValue(vec3 pos) {
//    float distance = length(pos);
//    return distance <= FogStart ? 0.0 : (distance < FogEnd ? smoothstep(FogStart, FogEnd, distance) : 1.0);
//}
//?} else if <= 26.2 {
//#version 150
//#define LS_LOC(n)
//
//#moj_import <minecraft:fog.glsl>
//#moj_import <minecraft:dynamictransforms.glsl>
//#moj_import <minecraft:projection.glsl>
//?} else {
#version 330
#extension GL_ARB_separate_shader_objects : require
#define LS_LOC(n) layout(location = n)

#include <minecraft:fog.glsl>
#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
//?}

LS_LOC(0) in vec3 Position;
LS_LOC(1) in vec2 UV0;
LS_LOC(2) in vec4 Color;

LS_LOC(0) out vec2 texCoord0;
LS_LOC(1) out vec4 vertexColor;
LS_LOC(2) out float fogFade;

//? if >= 1.21.6 {
float lsFogValue(vec3 pos) {
    return total_fog_value(fog_spherical_distance(pos), fog_cylindrical_distance(pos), FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd);
}
//?}

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = UV0;
    vertexColor = Color;
    fogFade = 1.0 - lsFogValue(Position);
}
