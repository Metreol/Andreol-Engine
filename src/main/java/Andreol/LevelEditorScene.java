package Andreol;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
            "// - The 'a' prefix stands for attribute, as in vertex attribute.\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "// - The 'f' prefix stands for fragment, as this is passed to the\n" +
            "// fragment shader.\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main() {\n" +
            "    fColor = aColor;\n" +
            "    // - gl_Position specifically MUST be defined.\n" +
            "    // - The vector4 uses the 3 values from aPos and 1.0 as the\n" +
            "    // 4th value.\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main() {\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgramID;

    // This is the data we'll use to create the VBO: Vertex Buffer Object
    // - This defines the vertices in terms of their attributes (coordinates, color, etc)
    // - IMPORTANT: Positions are normalised, so x and y are between -1 and
    // 1 (left to right, down to up, back to forward)
    private float[] vertexArray = {
        // position         // color
//        .5f, -.5f, .0f,     1.0f, .0f, .0f, 1.0f, // Bottom right 0
//        -.5f, .5f, .0f,     .0f, 1.0f, .0f, 1.0f, // Top left 1
//        .5f, .5f, .0f,      .0f, .0f, 1.0f, 1.0f, // Top right 2
//        -.5f, -.5f, 0f,     1.0f, 1.0f, .0f, 1.0f // Bottom left 3

            .5f, -.5f, .0f,     .0f, 1.0f, .0f, 1.0f, // Bottom right 0
            -.5f, .5f, .0f,     .0f, 1.0f, .0f, 1.0f, // Top left 1
            .5f, .5f, .0f,      1.0f, .0f, 1.0f, 1.0f, // Top right 2
            -.5f, -.5f, 0f,     1.0f, .0f, 1.0f, 1.0f // Bottom left 3
    };

    /*
    This is the data we'll use to create the EBO: Element Buffer Object.
    - This describes where the vertices are in the VBO above.
    - IMPORTANT: This MUST be in counter-clockwise order.

        1 x       x 2


        3 x       x 0

    - So for creating two triangles from the above vertices
    (with the split being top left to bottom right):
        BottomRight, TopLeft, BottomLeft,
        and
        TopRight, TopLeft, BottomRight

     */
    private int[] elementArray = {
            2, 1, 0, // Top Right Triangle (Could this be 0,2,1 or 1,0,2?)
            0, 1, 3 // Bottom Left Triangle (Could this be 1,3,0 or 3,0,1?)
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene() {

    }

    public void init() {
        // Compile Shaders
        vertexID = compileShader(GL_VERTEX_SHADER, vertexShaderSrc);
        fragmentID = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSrc);

        // Link Shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        // Check for linking errors
        if (glGetProgrami(shaderProgramID, GL_LINK_STATUS) == GL_FALSE) {
            System.out.printf("ERROR: 'defaultShader.glsl'\n\tShader's '%s' linking failed.\n%s",
                    glGetProgrami(shaderProgramID, GL_ATTACHED_SHADERS),
                    glGetProgramInfoLog(shaderProgramID));
            assert false : "";
        }

        // ==========================================================
        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        // ==========================================================
        // Create the VAO
        // - Not sure what this actually does here, I can comment it out and everything
        // still works.
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create VBO and upload the vertex buffer ==========
        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        // Flip() is required here to orient buffer correctly for OpenGL, will throw error if not flipped.
        vertexBuffer.put(vertexArray).flip();
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create EBO (indices) and upload ==================
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip(); // Same deal here as with above flip().
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3; // x, y, z
        int colorSize = 4; // r, g, b, a
        // You have to tell the GPU the size of the data you are passing, in this case
        // it's all floats and floats are 4 bytes
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        // The 1st param, index, is the index (location) defined in the shaders.
        // See strings above or default.glsl, specifically:
        //      layout (location=0) in vec3 aPos;
        //      layout (location=1) in vec4 aColor;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    // returns the shaderID
    private int compileShader(int shaderType, String shaderSrc) {
        // First: Load and compile vertex shader.
        int shaderID = glCreateShader(shaderType);
        // Pass Shader source to the GPU
        glShaderSource(shaderID, shaderSrc);
        glCompileShader(shaderID);

        // Check for errors in compilation
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            int infoLogLen = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
            System.out.printf("ERROR: 'defaultShader.glsl'\n\tShader '%s' compilation failed.", glGetShaderi(shaderID, GL_SHADER_TYPE));
            System.out.println(glGetShaderInfoLog(shaderID)); // TODO Is there a diff when using length of infoLog?
            System.out.println(glGetShaderInfoLog(shaderID, infoLogLen));
            assert false : "";
        }
        return shaderID;
    }

    @Override
    public void update(double deltaTime) {
        // Bind shader program
        glUseProgram(shaderProgramID);
        // Bind the VAO we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0); // '0' means bind to nothing.
        glUseProgram(0); // Same as above.
    }
}
