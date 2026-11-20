//? if <= 1.21.5 {
//#version 150
//#define LS_LOC(n)
//
//uniform mat4 ModelViewMat;
//uniform mat4 ProjMat;
//uniform float FogStart;
//uniform float FogEnd;
//
//float lsFogEnd(vec3 dir) {
//    return FogEnd > FogStart ? FogEnd / max(max(length(dir.xz), abs(dir.y)), 0.001) : 1.0e9;
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

LS_LOC(0) out vec3 relPos;
LS_LOC(1) out vec4 vertexColor;

//? if >= 1.21.6 {
float lsFogEnd(vec3 dir) {
    float environmental = FogEnvironmentalEnd > FogEnvironmentalStart ? FogEnvironmentalEnd : 1.0e9;
    float renderDistance = FogRenderDistanceEnd > FogRenderDistanceStart ? FogRenderDistanceEnd / max(fog_cylindrical_distance(dir), 0.001) : 1.0e9;
    return min(environmental, renderDistance);
}
//?}

void main() {
    vec3 dir = normalize(Position);
    gl_Position = ProjMat * ModelViewMat * vec4(dir * min(lsFogEnd(dir), 100000.0), 1.0);
    if (gl_Position.w > 0.0) {
        float farPlane = ProjMat[2][2] < -0.5 ? 1.0 : (ProjMat[2][2] > 0.5 ? -1.0 : 0.0);
        float depth = gl_Position.z / gl_Position.w;
        depth = farPlane > 0.5 ? min(depth, farPlane) : max(depth, farPlane);
        gl_Position.z = depth * gl_Position.w;
    }
    relPos = Position;
    vertexColor = Color;
}
