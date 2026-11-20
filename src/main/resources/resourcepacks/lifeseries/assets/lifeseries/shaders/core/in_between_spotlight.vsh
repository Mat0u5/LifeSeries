//? if <= 1.21.5 {
//#version 150
//#define LS_LOC(n)
//
//uniform mat4 ModelViewMat;
//uniform mat4 ProjMat;
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

LS_LOC(0) out vec3 relPos;
LS_LOC(1) out vec4 vertexColor;
LS_LOC(2) out vec3 eye;

void main() {
    mat4 viewProjection = ProjMat * ModelViewMat;
    gl_Position = viewProjection * vec4(Position, 1.0);
    vec4 eyePos = inverse(viewProjection) * vec4(0.0, 0.0, 1.0, 0.0);
    eye = eyePos.xyz / eyePos.w;
    relPos = Position;
    vertexColor = Color;
}
