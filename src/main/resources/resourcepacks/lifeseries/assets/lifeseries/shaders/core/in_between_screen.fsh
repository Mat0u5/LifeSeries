//? if <= 26.2 {
//#version 150
//#define LS_LOC(n)
//?} else {
#version 330
#extension GL_ARB_separate_shader_objects : require
#define LS_LOC(n) layout(location = n)
//?}

uniform sampler2D Sampler0;

LS_LOC(0) in vec2 screenUv;
LS_LOC(1) in vec2 extra;
LS_LOC(2) in vec4 params;

LS_LOC(0) out vec4 fragColor;

const int LS_BLUR_SAMPLES = 12;
const float LS_RIPPLE_PERIOD = 0.455;
const float LS_RIPPLE_SCALE = 0.085;
const float LS_RIPPLE_POWER = 0.7;
const float LS_RIPPLE_START = 0.002;
const float LS_RIPPLE_END = 0.0035;
const vec3 LS_WASH = vec3(0.80, 0.78, 0.83);
const vec2 LS_LIGHT = vec2(-0.33, 0.94);
const float LS_LIGHT_STEP = 0.035;

float lsHash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

float lsNoise(vec2 p) {
    vec2 cell = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = lsHash(cell);
    float b = lsHash(cell + vec2(1.0, 0.0));
    float c = lsHash(cell + vec2(0.0, 1.0));
    float d = lsHash(cell + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float lsFbm(vec2 p) {
    float value = 0.0;
    float amplitude = 0.5;
    for (int i = 0; i < 4; i++) {
        value += amplitude * lsNoise(p);
        p = mat2(1.6, 1.2, -1.2, 1.6) * p + vec2(3.1, 1.7);
        amplitude *= 0.5;
    }
    return value / 0.9375;
}

vec2 lsCentered(vec2 uv, float aspect) {
    return vec2((uv.x - 0.5) * aspect, uv.y - 0.5);
}

float lsRadius(vec2 centerd, float aspect) {
    return length(centerd) / (0.5 * length(vec2(aspect, 1.0)));
}

vec3 lsScene(vec2 uv, float blur) {
    if (blur <= 0.001) {
        return texture(Sampler0, uv).rgb;
    }
    vec2 offset = uv - 0.5;
    float zoom = 1.0 / (1.0 + 0.5 * blur);
    float spread = 0.1 * blur;
    float split = 0.015 * blur;
    float jitter = fract(52.9829189 * fract(dot(gl_FragCoord.xy, vec2(0.06711056, 0.00583715))));
    vec3 sum = vec3(0.0);
    for (int i = 0; i < LS_BLUR_SAMPLES; i++) {
        float scale = zoom * (1.0 - spread * (float(i) + jitter) / float(LS_BLUR_SAMPLES));
        sum.r += texture(Sampler0, 0.5 + offset * scale * (1.0 + split)).r;
        sum.g += texture(Sampler0, 0.5 + offset * scale).g;
        sum.b += texture(Sampler0, 0.5 + offset * scale * (1.0 - split)).b;
    }
    return sum / float(LS_BLUR_SAMPLES);
}

vec2 lsRipple(vec2 uv, float progress, float time, float aspect) {
    vec2 centerd = lsCentered(uv, aspect);
    float radius = lsRadius(centerd, aspect);
    if (radius < 1.0e-4) return uv;
    float phase = 6.2831853 * (pow(radius / LS_RIPPLE_SCALE, LS_RIPPLE_POWER) - time / LS_RIPPLE_PERIOD);
    float push = mix(LS_RIPPLE_START, LS_RIPPLE_END, progress) * smoothstep(0.0, 0.1, progress) * smoothstep(0.0, 0.06, radius);
    centerd *= 1.0 - push * sin(phase) / radius;
    return clamp(vec2(centerd.x / aspect + 0.5, centerd.y + 0.5), vec2(0.001), vec2(0.999));
}

float lsCloudDensity(vec2 uv, float progress, float time, float aspect) {
    vec2 centerd = lsCentered(uv, aspect);
    float radius = lsRadius(centerd, aspect);
    float angle = 0.35 * progress * (1.0 - radius) + 0.02 * time;
    vec2 q = mat2(cos(angle), sin(angle), -sin(angle), cos(angle)) * centerd * (2.0 + 0.7 * progress) + vec2(-0.06, 0.04) * time + vec2(3.7, 2.59);
    float front = 1.35 - 1.7 * pow(clamp(progress / 0.98, 0.0, 1.0), 1.5);
    return (radius - front + (lsFbm(q) - 0.5) * 0.6) / 0.28;
}

vec3 lsSwallowSmoke(vec3 scene, vec2 uv, float progress, float time, float aspect) {
    scene = mix(scene, LS_WASH, 0.55 * smoothstep(0.45, 1.0, progress));
    float density = lsCloudDensity(uv, progress, time, aspect);
    float ahead = lsCloudDensity(uv + LS_LIGHT * LS_LIGHT_STEP * vec2(1.0 / aspect, 1.0), progress, time, aspect);
    float lit = clamp(0.62 - 1.6 * (ahead - density) + 0.2 * smoothstep(0.5, 2.5, density), 0.0, 1.0);
    float white = smoothstep(0.2, 0.8, progress);
    vec3 shadow = mix(vec3(0.24, 0.24, 0.28), vec3(0.52, 0.52, 0.57), white);
    vec3 light = mix(vec3(0.56, 0.56, 0.60), vec3(1.0), white);
    float whiten = smoothstep(0.65, 1.0, progress);
    vec3 color = mix(mix(shadow, light, mix(lit, 1.0, whiten)), vec3(1.0), whiten * 0.7);
    scene = mix(scene, color, smoothstep(0.0, 1.0, density));
    return mix(scene, vec3(1.0), smoothstep(0.92, 1.0, progress));
}

void main() {
    float cover = params.r;
    float blur = params.g;
    float swallow = params.b;
    float time = extra.x;
    float aspect = max(extra.y, 0.01);

    vec3 color = lsScene(swallow > 0.0 ? lsRipple(screenUv, swallow, time, aspect) : screenUv, blur);
    if (swallow > 0.0) {
        color = lsSwallowSmoke(color, screenUv, swallow, time, aspect);
    }
    color = mix(color, vec3(params.a > 0.75 ? 1.0 : 0.0), cover);
    fragColor = vec4(color, 1.0);
}
