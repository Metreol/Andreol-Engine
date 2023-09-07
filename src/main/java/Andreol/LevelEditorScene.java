package Andreol;

import static org.lwjgl.opengl.GL20.*;

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

    // Positions are normalised, so x and y are between -1 and
    // 1 (left to right, down to up, back to forward)
    private float[] vertexArray = {
        // position         // color
        .5f, -.5f, .0f,     1.0f, .0f, .0f, 1.0f, // Bottom right 0
        -.5f, .5f, .0f,     .0f, 1.0f, .0f, 1.0f, // Top left 1
        .5f, .5f, .0f,      .0f, .0f, 1.0f, 1.0f, // Top right 2
        -.5f, -.5f, 0f,     1.0f, 1.0f, .0f, 1.0f // Bottom left 3
    };

    /* IMPORTANT: This MUST be in counter-clockwise order.
     So for creating two triangles from the below vertices
     (with the split being top left to bottom right):
     BottomRight, TopLeft, BottomLeft,
     and
     TopRight, TopLeft, BottomRight
     */
    private int[] elementArray = {
            /*
                  1 x       x 2


                  3 x       x 0
             */
            2, 1, 0, // Top Right Triangle (Could this be 0,2,1 or 1,0,2?)
            0, 1, 3 // Bottom Left Triangle (Could this be 1,3,0 or 3,0,1?)
    };

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

        // Generate VAO, VBO, and EBO buffer objects, and send to GPU

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

    }
}
