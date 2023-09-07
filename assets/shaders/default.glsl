#type vertex
#version 330 core
// - The 'a' prefix stands for attribute, as in vertex attribute.
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;

// - The 'f' prefix stands for fragment, as this is passed to the
// fragment shader.
out vec4 fColor;

void main() {
    fColor = aColor;
    // - gl_Position specifically MUST be defined.
    // - The vector4 uses the 3 values from aPos and 1.0 as the
    // 4th value.
    gl_Position = vec4(aPos, 1.0);
}