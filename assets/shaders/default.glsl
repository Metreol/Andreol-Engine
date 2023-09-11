#type vertex
#version 330 core
// - The 'a' prefix stands for attribute, as in vertex attribute.
// - These locations map to the VAO attributes defined in LevelEditorScene
// with glVertexAttribPointer(), which itself uses these location values.
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;

uniform mat4 uProjection;
uniform mat4 uView;

// - The 'f' prefix stands for fragment, as this is passed to the
// fragment shader.
out vec4 fColor;

void main() {
    fColor = aColor;
    // - gl_Position specifically MUST be defined.
    // - The vector4 uses the 3 values from aPos and 1.0 as the
    // 4th value.
    // - This calculation is used to determine the NDC (Normalized Device Cooridnates):
    //      projectionMatrix * viewMatrix * worldCoordinates = NDC
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

in vec4 fColor;

out vec4 color;

void main() {
    color = fColor;
}