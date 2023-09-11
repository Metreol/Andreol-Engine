package renderer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    private int shaderProgramID;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] shaders = source.split("#type +[A-Za-z]+");

            Pattern typePattern = Pattern.compile("#type +([A-Za-z]+)");
            Matcher typeMatcher = typePattern.matcher(source);
            int i = 0;
            while (typeMatcher.find()) {
                String shaderType = typeMatcher.group(1);
                if (shaderType.equalsIgnoreCase("vertex")) {
                    vertexSource = shaders[i + 1];
                } else if (shaderType.equalsIgnoreCase("fragment")) {
                    fragmentSource = shaders[i + 1];
                } else {
                    throw new IOException(String.format("Unexpected shader type '%s' in '%s'.", shaderType, filepath));
                }
                i++;
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
            assert false : String.format("Error: Could not open file for shader: '%s'", filepath);
        }
    }

    public void compileAndLink() {
        link(compile(GL_VERTEX_SHADER, vertexSource),
                compile(GL_FRAGMENT_SHADER, fragmentSource));
    }

    private int compile(int type, String source) {
        // First: Load and compile vertex shader.
        int shaderID = glCreateShader(type);
        // Pass Shader source to the GPU
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        // Check for errors in compilation
        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
            int infoLogLen = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
            assert false : String.format("\nERROR: Shader '%s' compilation failed.\n%s",
                    filepath, glGetShaderInfoLog(shaderID, infoLogLen));
        }
        return shaderID;
    }

    private void link(int vertexID, int fragmentID) {
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        // Check for linking errors
        if (glGetProgrami(shaderProgramID, GL_LINK_STATUS) == GL_FALSE) {
            System.out.printf("ERROR: '%s'\n\tShader's '%s' linking failed.\n%s",
                    glGetProgrami(shaderProgramID, GL_ATTACHED_SHADERS),
                    glGetProgramInfoLog(shaderProgramID),
                    filepath);
            assert false : "";
        }
    }

    public void use() {
        // Bind shader program
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0); // '0' means bind to nothing.
    }

    // Must be called after 'shader.use()' as it needs an existing shader.
    public void uploadMat4f(String varName, Matrix4f mat4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        // Capacity is 16 as it's a 4x4 matrix.
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        mat4f.get(matrixBuffer);
        glUniformMatrix4fv(varLocation, false, matrixBuffer);
    }
}
