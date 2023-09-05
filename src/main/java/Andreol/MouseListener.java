package Andreol;

import static org.lwjgl.glfw.GLFW.*;

public class MouseListener {
    private static MouseListener mouseListener;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    // Add catch in case there are more than 3 buttons (probably not worth covering more than this).
    private final boolean[] mouseButtonPressed = new boolean[3];
    private boolean isDragging;

    private MouseListener() {
        this.scrollX = 0;
        this.scrollY = 0;
        this.xPos = 0;
        this.yPos = 0;
        this.lastX = 0;
        this.lastY = 0;
    }

    public static MouseListener get() {
        if (mouseListener == null) {
            mouseListener = new MouseListener();
        }
        return mouseListener;
    }

    public static void cursorPositionCallback(long window, double newXPos, double newYPos) {
        get();
        mouseListener.lastX = mouseListener.xPos;
        mouseListener.lastY = mouseListener.yPos;
        mouseListener.xPos = newXPos;
        mouseListener.yPos = newYPos;
        boolean draggingCheck = false;
        for (boolean buttonPressed : mouseListener.mouseButtonPressed) {
            if (buttonPressed) {
                draggingCheck = true;
                break;
            }
        }
        mouseListener.isDragging = draggingCheck;
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        get();
        if (action == GLFW_PRESS) {
            if (button < mouseListener.mouseButtonPressed.length) {
                mouseListener.mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            mouseListener.mouseButtonPressed[button] = false;
            mouseListener.isDragging = false;
        }
    }

    public static void scrollCallback(long window, double xOffset, double yOffset) {
        get();
        mouseListener.scrollX = xOffset;
        mouseListener.scrollY = yOffset;
    }

    public static void endFrame() {
        get();
        mouseListener.scrollX = 0;
        mouseListener.scrollY = 0;
        mouseListener.lastX = mouseListener.xPos;
        mouseListener.lastY = mouseListener.yPos;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().xPos;
    }

    public static float getDeltaX() {
        get();
        return (float) (mouseListener.lastX - mouseListener.xPos);
    }

    public static double getDeltaY() {
        get();
        return (float) (mouseListener.lastY - mouseListener.yPos);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        get();
        if (button < mouseListener.mouseButtonPressed.length) {
            return mouseListener.mouseButtonPressed[button];
        } else {
            return false;
        }
    }
}
