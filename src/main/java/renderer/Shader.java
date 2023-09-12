package renderer;

import org.joml.*;
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
    private boolean inUse = false;
    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        readShaderFile();
    }

    private void readShaderFile() {
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] shaders = source.split("#type +[A-Za-z]+");

            Pattern typePattern = Pattern.compile("#type +([A-Za-z]+)");
            Matcher typeMatcher = typePattern.matcher(source);
            int i = 0;
            while (typeMatcher.find()) {
                String shaderType = typeMatcher.group(1);
                if (shaderType.equalsIgnoreCase("vertex")) {
                    // - 'i + 1' because split() will give us the string before the
                    // "#type ..." line, even if it's empty.
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
        if (!inUse) {
            // Bind shader program
            glUseProgram(shaderProgramID);
            inUse = true;
        }
    }

    public void detach() {
        glUseProgram(0); // '0' means bind to nothing.
        inUse = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        // Capacity is 16 as it's a 4x4 matrix.
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        mat4f.get(matrixBuffer);
        glUniformMatrix4fv(varLocation, false, matrixBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        // Capacity is 9 as it's a 3x3 matrix.
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(9);
        mat3f.get(matrixBuffer);
        glUniformMatrix3fv(varLocation, false, matrixBuffer);
    }

    public void uploadMat2f(String varName, Matrix2f mat2f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        // Capacity is 4 as it's a 2x2 matrix.
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(4);
        mat2f.get(matrixBuffer);
        glUniformMatrix3fv(varLocation, false, matrixBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec4f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec4f.x, vec4f.y, vec4f.z, vec4f.w);
    }

    public void uploadVec3f(String varName, Vector3f vec3f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec3f.x, vec3f.y, vec3f.z);
    }

    public void uploadVec2f(String varName, Vector2f vec2f) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec2f.x, vec2f.y);
    }

    public void uploadFloat(String varName, float val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, val);
    }

    public void uploadInt(String varName, int val) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, val);
    }
}
