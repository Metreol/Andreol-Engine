package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
    private String filePath;
    private int textureID;

    public Texture(String filePath) {
        this.filePath = filePath;

        this.textureID = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);

        // Set texture params =========
        // Repeat image in both directions
        // S and T map to X (left/right) and Y (up/down) directions.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // When stretching (MIN... no idea why it's MIN!) textures, pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        // When shrinking (MAGnifying) textures, also pixelate
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load(filePath, width, height, channels, 0);

        if (image != null) {
            if (channels.get(0) == 3) { // RGB
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0),
                        height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
            } else if (channels.get(0) == 4) { // RGBA
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0),
                        height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
            } else {
                assert false : "Error: (Texture) Unknown number of channels. Can handle rgb (3) and rgba (4) but found : '" + channels.get(0) + "'.";
            }
        } else {
            assert false : "Error: (Texture) Could not load image '" + filePath + "'.";
        }

        stbi_image_free(image); // Release image data, not doing this causes memory leak.
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
