#type vertex
#version 330 core
// - The 'a' prefix stands for attribute, as in vertex attribute.
// - These locations map to the VAO attributes defined in LevelEditorScene
// with glVertexAttribPointer(), which itself uses these location values.
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;
layout (location=2) in vec2 aTextureCoords;

// - The above attributes are defined per object, but for things that don't change
// across objects we can upload them using 'uniform' as so.
// - The actual upload happens using our Shader.uploadMat4f() method. Note that
// this requires using the specific var name ("uView", etc) to identify what is being
// uploaded as there is no "location" like with the "layout" attributes above.
uniform mat4 uProjection;
uniform mat4 uView;

// - The 'f' prefix stands for fragment, as this is passed to the
// fragment shader.
// - This is the output of main, and you MUST have an equivalent 'in' in the Fragment
// shader for every "out" in the Vertex shader. (See 'in vec4 fColor' below)
out vec4 fColor;
out vec2 fTextureCoords;

// Entry point for the program, where any changes/transformations happen.
void main() {
    fColor = aColor;
    fTextureCoords = aTextureCoords;
    // - gl_Position specifically MUST be defined.
    // - The vector4 uses the 3 values from aPos and 1.0 as the
    // 4th value.
    // - This calculation is used to determine the NDC (Normalized Device Cooridnates):
    //      projectionMatrix * viewMatrix * worldCoordinates = NDC
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

uniform float uTime;
uniform sampler2D TEXTURE_SAMPLER;

in vec4 fColor;
in vec2 fTextureCoords;

// - I THINK this is the one output for the fragment shader and is passed to the next
// stage in the graphics pipeline.
out vec4 color;

// Transforms the object to a black and white version.
vec4 blackAndWhite() {
    float avg = (fColor.r + fColor.g, fColor.b) / 3;
    return vec4(avg, avg, avg, 1);
}

// Fluctuates brightness using a sin wave.
vec4 colorFluctuation() {
    return sin(uTime) * fColor;
}

// Uses noise to distort object colors
// Details here: http://science-and-fiction.org/rendering/noise.html
// Above link also has details about Perlin Noise.
vec4 noiseify() {
    float noise = fract(sin(dot(fColor.xy, vec2(12.9898,78.233))) * 43758.5453);
    return fColor * noise;
}

void main() {
    // color = fColor;
    color = texture(TEXTURE_SAMPLER, fTextureCoords);
}