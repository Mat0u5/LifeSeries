//? if <= 1.21.5 {
//#version 150
//#define LS_LOC(n)
//
//uniform mat4 ModelViewMat;
//uniform mat4 ProjMat;
//uniform mat4 TextureMat;
//?} else if <= 26.2 {
//#version 150
//#define LS_LOC(n)
//
//#moj_import <minecraft:dynamictransforms.glsl>
//#moj_import <minecraft:projection.glsl>
//?} else {
#version 330
#extension GL_ARB_separate_shader_objects : require
#define LS_LOC(n) layout(location = n)

#include <minecraft:dynamictransforms.glsl>
#include <minecraft:projection.glsl>
//?}

LS_LOC(0) in vec3 Position;
LS_LOC(1) in vec2 UV0;
LS_LOC(2) in vec4 Color;

LS_LOC(0) out vec3 localPos;
LS_LOC(1) out vec3 relPos;
LS_LOC(2) out vec4 vertexColor;
LS_LOC(3) out vec3 eyeLocal;

vec3 lsToWindow(vec3 pos) {
    vec4 axes = TextureMat[0];
    return vec3(axes.x * pos.x + axes.y * pos.z + axes.z, pos.y + TextureMat[1].x, axes.x * pos.z - axes.y * pos.x + axes.w);
}

void main() {
    mat4 viewProjection = ProjMat * ModelViewMat;
    gl_Position = viewProjection * vec4(Position, 1.0);
    localPos = lsToWindow(Position);
    vec4 eye = inverse(viewProjection) * vec4(0.0, 0.0, 1.0, 0.0);
    eyeLocal = lsToWindow(eye.xyz / eye.w);
    relPos = Position;
    vertexColor = Color;
}
