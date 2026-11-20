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

LS_LOC(0) out vec2 screenUv;
LS_LOC(1) out vec2 extra;
LS_LOC(2) out vec4 params;

// Transition screen
void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    screenUv = gl_Position.xy / gl_Position.w * 0.5 + 0.5;
    extra = UV0;
    params = Color;
}
