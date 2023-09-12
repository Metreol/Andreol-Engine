package Andreol;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

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

            100f, 0f, .0f,     .0f, 1.0f, .0f, 1.0f, // Bottom right 0
            0f, 100f, .0f,     .0f, 1.0f, .0f, 1.0f, // Top left 1
            100f, 100f, .0f,      1.0f, .0f, 1.0f, 1.0f, // Top right 2
            0f, 0f, 0f,     1.0f, .0f, 1.0f, 1.0f // Bottom left 3
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

    private Shader defaultShader;

    private int vaoID, vboID, eboID;

    // NOT IMPORTANT, JUST FOR SCREENSAVER
    float cameraMoveVelocityX = 400.0f;
    float cameraMoveVelocityY = 300.0f;

    public LevelEditorScene() {

    }

    public void init() {
        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("D:\\cmasenv\\workspace\\java\\Andreol-Engine\\assets\\shaders\\default.glsl");
        defaultShader.compileAndLink(); // Move this into constructor?

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

    @Override
    public void update(double deltaTime) {
        screenSaver(deltaTime);
        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTimeFloat());
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
        defaultShader.detach();
    }

    // JUST FOR FUN, NOT IMPORTANT!
    // NOTE: It is the Camera moving, not the box.
    private void screenSaver(double deltaTime) {
        // -32f * 40f = SCREEN WIDTH
        // 100f = WIDTH OF OBJECT
        if ((cameraMoveVelocityX > 0 && camera.position.x - deltaTime * cameraMoveVelocityX < (-32f * 40f) + 100f) ||
                (cameraMoveVelocityX < 0 && camera.position.x - deltaTime * cameraMoveVelocityX > 0f)) {
            cameraMoveVelocityX *= -1;
        }
        // -32f * 21f = SCREEN HEIGHT
        // 100f = HEIGHT OF OBJECT
        if ((cameraMoveVelocityY > 0 && camera.position.y - deltaTime * cameraMoveVelocityY < (-32f * 21) + 100f) ||
                (cameraMoveVelocityY < 0 && camera.position.y - deltaTime * cameraMoveVelocityY > 0f)) {
            cameraMoveVelocityY *= -1;
        }

        camera.position.x -= deltaTime * cameraMoveVelocityX;
        camera.position.y -= deltaTime * cameraMoveVelocityY;
    }
}
