#type vertex
#version 330 core
// - The 'a' prefix stands for attribute, as in vertex attribute.
// - These locations map to the VAO attributes defined in LevelEditorScene
// with glVertexAttribPointer(), which itself uses these location values.
layout (location=0) in vec3 aPos;
layout (location=1) in vec4 aColor;

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

// Entry point for the program, where any changes/transformations happen.
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

// - I THINK this is the one output for the fragment shader and is passed to the next
// stage in the graphics pipeline.
out vec4 color;

void main() {
    color = fColor;
}