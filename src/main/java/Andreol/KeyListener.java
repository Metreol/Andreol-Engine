package Andreol;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

// GLFW Keys: https://www.glfw.org/docs/3.3/group__keys.html
public class KeyListener {
    private static KeyListener keyListener;
    private final boolean[] keysPressed = new boolean[350];

    private KeyListener() {

    }

    public static KeyListener get() {
        if (keyListener == null) {
            keyListener = new KeyListener();
        }
        return keyListener;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods) {
        get();
        if (action == GLFW_PRESS) {
            keyListener.keysPressed[key] = true;
        } else if (action == GLFW_RELEASE) {
            keyListener.keysPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int key) {
        return get().keysPressed[key];
    }
}
