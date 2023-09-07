package Andreol;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import util.Time;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/*
A lot of this code, namely init() and some of loop(), is taken/adapted from
the LWJGL Getting Started page: https://www.lwjgl.org/guide
 */
public class Window {
    public float r, g, b, a;

    private int width, height;
    private String title;
    // This long is the address in memory where the window is.
    private long glfwWindow;
    private boolean fadeToBlack;
    private double framesPerSecond;

    private static Window window = null;
    private static Scene currentScene;

    private Window() {
        this.width = 1920;
        this.height = 1080;
        this.title = "MyGame";
        this.r = 1;
        this.g = 1;
        this.b = 1;
        this.a = 1;
    }

    public static void changeScene(int sceneIndex) {
        switch(sceneIndex) {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                break;
            default:
                assert false : "Unknown scene index: " + sceneIndex;
                System.out.printf("There is no scene type bound to index %s", sceneIndex);
                break;
        }
    }

    public static Window get() {
        if (window == null) {
            window = new Window();
        }
        return window;
    }

    public void run() {
        System.out.println("Hello LWJGL" + Version.getVersion() + "!");

        init();
        loop();

        cleanUp();
    }

    public void init() {
        // Set up an ERROR callback.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialise GLFW (Game Library Framework)
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to init GLFW.");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // Only make it visible once it's finished being created.
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        // Create the Window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create the GLFW Window.");
        }

        glfwSetCursorPosCallback(glfwWindow, MouseListener::cursorPositionCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::scrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

        // Make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);
        //
        // Very roughly the options are:
        // 0 - Unlimited FPS, even beyond monitor/machine capability.
        // 1 - Capped by monitor capability, but hits max if it can.
        // >=2 - Half the monitors capability (Waits 2 Intervals for 2, 3 for 3 etc)
        glfwSwapInterval(1);

        // Make window visible
        glfwShowWindow(glfwWindow);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Window.changeScene(0);
    }

    public void loop() {
        double frameStartTime = Time.getTime();
        double frameEndTime;
        double deltaTime = -1d;
        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                System.out.println(framesPerSecond);
            }

            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (deltaTime >= 0) {
                currentScene.update(deltaTime);
            }

            glfwSwapBuffers(glfwWindow);

            frameEndTime = Time.getTime();
            deltaTime = frameEndTime - frameStartTime;
            framesPerSecond = 1D / deltaTime;
            // Better to set startTime here as any interrupts between frames will
            // be included in calculations
            frameStartTime = frameEndTime;
        }
   }

   // I believe the system SHOULD free up memory but the LWJGL docs include this
   private void cleanUp() {
       // Free memory
       Callbacks.glfwFreeCallbacks(glfwWindow);
       glfwDestroyWindow(glfwWindow);

       // Terminate GLFW and free the Error callback
       glfwTerminate();
       glfwSetErrorCallback(null).free();
   }
}
